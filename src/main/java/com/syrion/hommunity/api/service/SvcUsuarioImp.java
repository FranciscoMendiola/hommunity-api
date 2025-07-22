package com.syrion.hommunity.api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.syrion.hommunity.api.dto.in.DtoEstadoUsuariIn;
import com.syrion.hommunity.api.dto.in.DtoQrUsuarioIn;
import com.syrion.hommunity.api.dto.in.DtoUsuarioContraseñaIn;
import com.syrion.hommunity.api.dto.in.DtoUsuarioIn;
import com.syrion.hommunity.api.dto.out.DtoFamiliaPersonasOut;
import com.syrion.hommunity.api.dto.out.DtoUsuarioOut;
import com.syrion.hommunity.api.entity.Familia;
import com.syrion.hommunity.api.entity.QR;
import com.syrion.hommunity.api.entity.Usuario;
import com.syrion.hommunity.api.repository.FamiliaRepository;
import com.syrion.hommunity.api.repository.QrRepository;
import com.syrion.hommunity.api.repository.UsuarioRepository;
import com.syrion.hommunity.api.repository.ZonaRepository;
import com.syrion.hommunity.common.dto.ApiResponse;
import com.syrion.hommunity.common.mapper.MapperUsuario;
import com.syrion.hommunity.exception.ApiException;
import com.syrion.hommunity.exception.DBAccessException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SvcUsuarioImp implements SvcUsuario {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ZonaRepository zonaRepository;

    @Autowired
    private FamiliaRepository familiaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private QrRepository qrRepository;
    
    @Autowired
    private MapperUsuario mapper;
    
    @Autowired
    private SvcQr svcQr;
    
    @Value("${app.upload.dir}")
	private String uploadDir;
    
    @Override
    public boolean isRegistrador(String correo) {
        System.out.println("Verificando si es registrador para correo: " + correo);
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) {
            System.out.println("Usuario no encontrado para correo: " + correo);
            return false;
        }
        boolean isRegistrador = familiaRepository.existsByIdUsuarioRegistrador(usuario.getIdUsuario());
        System.out.println("Es registrador: " + isRegistrador + ", idUsuario: " + usuario.getIdUsuario());
        return isRegistrador;
    }

    @Override
    public ResponseEntity<DtoUsuarioOut> getUsuario(Long id) {
        try {
            Usuario usuario = validateId(id);

            DtoUsuarioOut usuarioOut = mapper.fromUsuarioToDtoUsuarioOut(usuario);
            return new ResponseEntity<>(usuarioOut, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

@Override
public ResponseEntity<ApiResponse> createUsuario(DtoUsuarioIn in) {
    try {
        MultipartFile file = in.getFotoIdentificacion();

        if (file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La imagen de identificación es obligatoria.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El archivo no es una imagen válida.");
        }

        //Validación de máximo de 5 integrantes por familia
        if (in.getIdFamilia() != null) {
            List<Usuario> usuariosAprobados = usuarioRepository.findUsuariosAprobadosPorFamilia(in.getIdFamilia());
            if (usuariosAprobados.size() >= 5) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Esta familia ya tiene el máximo de 5 integrantes permitidos.");
            }
        }


        // Encriptar la contraseña
        in.setContrasena(passwordEncoder.encode(in.getContrasena()));

        // Mapear y guardar usuario para obtener ID
        Usuario usuario = mapper.fromDtoUsuarioInToUsuario(in);
        usuario = usuarioRepository.save(usuario); // Aquí se genera el ID

        // Obtener extensión del archivo original
        String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."));
        String fileName = "img/usuario/usuario_" + usuario.getIdUsuario() + extension;

        // Ruta: {uploadDir}/img/usuario/usuario_#.png
        Path imagePath = Paths.get(uploadDir, fileName);
        Files.createDirectories(imagePath.getParent());

        // Guardar el archivo
        file.transferTo(imagePath.toFile());

        // Guardar la ruta en el usuario
        usuario.setFotoIdentificacion("uploads/" + fileName);
        usuarioRepository.save(usuario); // Actualizar con la ruta de imagen

        return new ResponseEntity<>(
                new ApiResponse("Usuario creado correctamente con imagen"),
                HttpStatus.CREATED
        );

    } catch (IOException e) {
        throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen: " + e.getMessage());
    } catch (DataAccessException e) {
        if (e.getLocalizedMessage().contains("chk_usuario_estado"))
            throw new ApiException(HttpStatus.BAD_REQUEST, "El status (estado) del usuario no está definido");
        if (e.getLocalizedMessage().contains("ux_usuario_correo"))
            throw new ApiException(HttpStatus.CONFLICT, "El correo ya está registrado");
        if (e.getLocalizedMessage().contains("fk_usuario_id_familia"))
            throw new ApiException(HttpStatus.NOT_FOUND, "El id de la familia no está registrado");
        if (e.getLocalizedMessage().contains("fk_usuario_id_rol"))
            throw new ApiException(HttpStatus.NOT_FOUND, "El id del rol no está registrado");
        if (e.getLocalizedMessage().contains("fk_usuario_id_zona"))
            throw new ApiException(HttpStatus.NOT_FOUND, "El id de la zona no está registrado");

        throw new DBAccessException(e);
    }
}



    @Override
    public ResponseEntity<ApiResponse> deleteUsuario(Long id) {
        try {
            validateId(id);

            usuarioRepository.deleteById(id);

            return new ResponseEntity<>(new ApiResponse("Usuario eliminado correctamente"), HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<List<DtoUsuarioOut>> getUsuariosPorZona(Long idZona) {
        try {
            if (!zonaRepository.existsById(idZona))
                throw new ApiException(HttpStatus.NOT_FOUND, "Zona no encontrada con id: " + idZona);

            List<Usuario> usuarios = usuarioRepository.findByIdZona(idZona);

            List<DtoUsuarioOut> usuariosOut = mapper.fromListUsuarioToDtoUsuarioOut(usuarios);

            return new ResponseEntity<>(usuariosOut, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<List<DtoUsuarioOut>> getUsuariosPendientesAdmin(Long idZona) {
        try {
            if (!zonaRepository.existsById(idZona)) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Zona no encontrada con id: " + idZona);
            }
            List<Usuario> usuarios = usuarioRepository.findByFamiliaYEstadoPendiente(idZona);
            List<DtoUsuarioOut> usuariosOut = mapper.fromListUsuarioToDtoUsuarioOut(usuarios);
            return new ResponseEntity<>(usuariosOut, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }
  
    @Override
    public ResponseEntity<List<DtoUsuarioOut>> getUsuariosPendientesResidente(Long idZona) {
        try {
            List<Usuario> usuarios = usuarioRepository.findUsuariosPendientesPorZonaConRegistrador(idZona);
            List<DtoUsuarioOut> usuariosOut = mapper.fromListUsuarioToDtoUsuarioOut(usuarios);
            return new ResponseEntity<>(usuariosOut, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    
  
    @Override
    public ResponseEntity<List<DtoFamiliaPersonasOut>> getUsuariosAprobadosPorFamilia(Long idFamilia) {
        try {
            List<Usuario> usuarios = usuarioRepository.findByIdFamiliaAndEstado(idFamilia, "APROBADO");
            List<DtoFamiliaPersonasOut> dtos = mapper.fromListUsuarioToDtoFamiliaPersonasOut(usuarios);
            return ResponseEntity.ok(dtos);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }


    @Override
    public ResponseEntity<List<DtoUsuarioOut>> getUsuariosPorFamilia(Long idFamilia) {
        try {
            if (!familiaRepository.existsById(idFamilia))
                throw new ApiException(HttpStatus.NOT_FOUND, "Familia no encontrada con id: " + idFamilia);

            List<Usuario> usuarios = usuarioRepository.findByIdFamilia(idFamilia);

            List<DtoUsuarioOut> usuariosOut = mapper.fromListUsuarioToDtoUsuarioOut(usuarios);

            return new ResponseEntity<>(usuariosOut, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> updateEstadoUsuario(Long idUsuario, DtoEstadoUsuariIn in, Authentication authentication) {
        System.out.println("Entrando a updateEstadoUsuario, idUsuario: " + idUsuario);
        System.out.println("Authentication: " + (authentication != null ? authentication.getName() : "null"));

        // Verificar autenticación
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("Fallo en autenticación: token nulo o no autenticado");
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Token requerido o inválido");
        }

        // Obtener usuario autenticado
        String correo = authentication.getName();
        System.out.println("Usuario autenticado correo: " + correo);
        Usuario usuarioAutenticado = usuarioRepository.findByCorreo(correo);
        if (usuarioAutenticado == null) {
            System.out.println("Usuario no encontrado para correo: " + correo);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado");
        }

        // Verificar que el usuario a actualizar existe y pertenece a la misma zona
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            System.out.println("Usuario no encontrado para idUsuario: " + idUsuario);
            throw new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        if (!usuario.getIdZona().equals(usuarioAutenticado.getIdZona())) {
            System.out.println("Zona no coincide: usuario=" + usuario.getIdZona() + ", autenticado=" + usuarioAutenticado.getIdZona());
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes actualizar usuarios de otra zona");
        }

        // Validar estado
        String nuevoEstado = in.getEstado();
        if (!nuevoEstado.equals("APROBADO") && !nuevoEstado.equals("PENDIENTE")) {
            System.out.println("Estado inválido: " + nuevoEstado);
            throw new ApiException(HttpStatus.BAD_REQUEST, "Estado inválido");
        }

        // Actualizar estado
        usuario.setEstado(nuevoEstado);
        usuarioRepository.save(usuario);
        System.out.println("Estado actualizado para idUsuario: " + idUsuario + ", nuevo estado: " + nuevoEstado);

        // Generar código QR si el estado es APROBADO
        if (nuevoEstado.equals("APROBADO")) {
            QR qrExistente = qrRepository.findByIdUsuario(idUsuario);
            if (qrExistente == null) {
                String codigoQr = UUID.randomUUID().toString();
                QR qr = new QR();
                qr.setIdUsuario(idUsuario);
                qr.setCodigo(codigoQr);
                qr.setFechaCreacion(LocalDateTime.now());
                qr.setVigente(true);
                qr.setUsosDisponibles(-1); // Ajusta según tus necesidades
                qr.setIdInvitado(null); // No es un invitado
                qrRepository.save(qr);
                System.out.println("Código QR generado para idUsuario: " + idUsuario + ", código: " + codigoQr);
            } else {
                System.out.println("Código QR ya existe para idUsuario: " + idUsuario);
            }
        }

        return ResponseEntity.ok(new ApiResponse("Estado actualizado correctamente"));
    }

    @Override
    public ResponseEntity<ApiResponse> updateContraseña(Long id, DtoUsuarioContraseñaIn in) {
        try {
            Usuario usuario = validateId(id);

            if (!passwordEncoder.matches(in.getContraseñaActual(), usuario.getContraseña()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");

            if (in.getNuevaContraseña().equals(in.getContraseñaActual()))
                throw new ApiException(HttpStatus.BAD_REQUEST, "La nueva contraseña no puede ser igual a la actual");

            usuario.setContraseña(passwordEncoder.encode(in.getNuevaContraseña()));

            usuarioRepository.save(usuario);
            return new ResponseEntity<>(new ApiResponse("Contraseña actualizada correctamente"), HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    private Usuario  validateId(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null)
            throw new ApiException(HttpStatus.NOT_FOUND, "El id del usuario no esta registrado.");

        return usuario;
    }

    @Override
    public ResponseEntity<List<DtoUsuarioOut>> getUsuariosPendientesPorZonaYRegistrador(Long idZona, Long idUsuarioRegistrador) {
        if (!zonaRepository.existsById(idZona)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Zona no encontrada con id: " + idZona);
        }
        if (!usuarioRepository.existsById(idUsuarioRegistrador)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Usuario registrador no encontrado con id: " + idUsuarioRegistrador);
        }
        if (!familiaRepository.existsByIdUsuarioRegistrador(idUsuarioRegistrador)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El usuario no es registrador de ninguna familia en la zona: " + idZona);
        }
        List<Usuario> pendientes = usuarioRepository.findByIdZonaAndEstadoAndUsuarioRegistrador(idZona, "PENDIENTE", idUsuarioRegistrador);
        List<DtoUsuarioOut> lista = mapper.fromListUsuarioToDtoUsuarioOut(pendientes);
        return ResponseEntity.ok(lista);
    }
}
