package com.syrion.hommunity.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.syrion.hommunity.api.dto.in.DtoEstadoUsuariIn;
import com.syrion.hommunity.api.dto.in.DtoQrResidenteIn;
import com.syrion.hommunity.api.dto.in.DtoUsuarioContraseñaIn;
import com.syrion.hommunity.api.dto.in.DtoUsuarioIn;
import com.syrion.hommunity.api.dto.out.DtoUsuarioOut;
import com.syrion.hommunity.api.entity.Usuario;
import com.syrion.hommunity.api.repository.FamiliaRepository;
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
    private MapperUsuario mapper;

    @Autowired
    private SvcQr svcQr;

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
            in.setContraseña(passwordEncoder.encode(in.getContraseña()));
            
            Usuario usuario = mapper.fromDtoUsuarioInToUsuario(in);
            
            usuarioRepository.save(usuario);

            System.err.println("Usuario guardado");

            return new ResponseEntity<>(new ApiResponse("Usuario creado correctamente"), HttpStatus.CREATED);
        } catch (DataAccessException e) {
            // Esta excepción no ocurrirá por el mapper
            if (e.getLocalizedMessage().contains("chk_usuario_estado"))
                throw new ApiException(HttpStatus.BAD_REQUEST, "El status (estado) del usuario no esta definido");

            if (e.getLocalizedMessage().contains("ux_usuario_correo"))
                throw new ApiException(HttpStatus.CONFLICT, "El correo ya esta registrado");

            if (e.getLocalizedMessage().contains("fk_usuario_id_familia"))
                throw new ApiException(HttpStatus.NOT_FOUND, "El id de la familia no esta registrado");

            // Ocurrirá si el rol RESIDENTE no esta en base de datos
            if (e.getLocalizedMessage().contains("fk_usuario_id_rol"))
                throw new ApiException(HttpStatus.NOT_FOUND, "El id del rol no esta registrado");

            if (e.getLocalizedMessage().contains("fk_usuario_id_zona"))
                throw new ApiException(HttpStatus.NOT_FOUND, "El id de la zona no esta registrado");

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
    public ResponseEntity<ApiResponse> updateEstadoUsuario(Long id, DtoEstadoUsuariIn in) {
        try {
            Usuario usuario = validateId(id);

            if (!in.getEstado().equalsIgnoreCase("aprobado") && !in.getEstado().equalsIgnoreCase("pendiente"))
                throw new ApiException(HttpStatus.BAD_REQUEST, "El status (estado) del usuario no está definido");

            if (!in.getEstado().equalsIgnoreCase(usuario.getEstado()))
                usuario.setEstado(in.getEstado());

            usuarioRepository.save(usuario);

            // Si el estado es aprobado, crear QR automáticamente
            if (in.getEstado().equalsIgnoreCase("aprobado")) {
                DtoQrResidenteIn qrIn = new DtoQrResidenteIn();
                qrIn.setIdUsuario(id);
                svcQr.createCodigoResidente(qrIn);
            }

            return new ResponseEntity<>(new ApiResponse("Usuario actualizado correctamente"), HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
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
}
