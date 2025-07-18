package com.syrion.hommunity.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.syrion.hommunity.api.dto.in.DtoAuthIn;
import com.syrion.hommunity.api.dto.in.DtoFamiliaIn;
import com.syrion.hommunity.api.dto.in.DtoQrUsuarioIn;
import com.syrion.hommunity.api.dto.in.DtoUsuarioRegistradorIn;
import com.syrion.hommunity.api.entity.Familia;
import com.syrion.hommunity.api.entity.QR;
import com.syrion.hommunity.api.entity.Usuario;
import com.syrion.hommunity.api.repository.FamiliaRepository;
import com.syrion.hommunity.api.repository.QrRepository;
import com.syrion.hommunity.api.repository.UsuarioRepository;
import com.syrion.hommunity.api.repository.ZonaRepository;
import com.syrion.hommunity.common.dto.ApiResponse;
import com.syrion.hommunity.common.mapper.MapperFamilia;
import com.syrion.hommunity.common.mapper.MapperQR;
import com.syrion.hommunity.exception.ApiException;
import com.syrion.hommunity.exception.DBAccessException;

@Service
public class SvcFamiliaImp implements SvcFamilia {

    @Autowired
    private FamiliaRepository familiaRepository;
    
    @Autowired
    private ZonaRepository zonaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private MapperFamilia mapper;

    @Autowired
    private MapperQR mapperQr;

    @Autowired
    private QrRepository qrRepository;
    
    @Override
    public ResponseEntity<Familia> getFamiliaPorId(Long id) {
        try {
            Familia familia = validateId(id);
            
            return new ResponseEntity<>(familia, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }
    
    @Override
    public ResponseEntity<List<Familia>> getFamiliasPorZona(Long idZona) {
        try {
            if (!zonaRepository.existsById(idZona))
                throw new ApiException(HttpStatus.NOT_FOUND, "Zona no encontrada con id: " + idZona);

            List<Familia> familias = familiaRepository.findFamiliasByIdZona(idZona);

            return new ResponseEntity<>(familias, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> createFamilia(DtoFamiliaIn in) {
        try {
            Familia familia = mapper.fromDtoFamiliaInTo(in);

            familiaRepository.save(familia);

            return new ResponseEntity<>(new ApiResponse("Familia creada correctamente"), HttpStatus.CREATED);
        } catch (DataAccessException e) {
            // No debería ocurrir error por el mapper
            if (e.getLocalizedMessage().contains("chk_familia_estado"))
                throw new ApiException(HttpStatus.BAD_REQUEST, "El el status (estado) de la familia ingresado no esta definido");

            if (e.getLocalizedMessage().contains("ux_familia_usuario_registrador"))
                throw new ApiException(HttpStatus.CONFLICT, "El id del usuario registrador ya esta asociado a una familia");

            if (e.getLocalizedMessage().contains("ux_id_casa"))
                throw new ApiException(HttpStatus.CONFLICT, "El id de la casa ya esta asociado a una familia");

            if (e.getLocalizedMessage().contains("fk_familia_id_casa"))
                throw new ApiException(HttpStatus.NOT_FOUND, "El id de la casa no esta registrado");

            // Esta excepción nunca se arrojara por la validación del mapper
            if (e.getLocalizedMessage().contains("fk_familia_id_usuario_registrador"))
                throw new ApiException(HttpStatus.NOT_FOUND, "El id del usuario registrador no existe");

            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> deleteFamilia(Long idFamilia) {
        try {
            validateId(idFamilia);

            familiaRepository.deleteById(idFamilia);

            return new ResponseEntity<>(new ApiResponse("Familia eliminada correctamente"), HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
public ResponseEntity<ApiResponse> updateUsuarioRegistrador(Long idFamilia, DtoUsuarioRegistradorIn in) {
    try {
        Familia familia = validateId(idFamilia);

        Usuario usuario = usuarioRepository.findById(in.getIdUsuarioRegistrador()).orElse(null);

        if (usuario == null)
            throw new ApiException(HttpStatus.NOT_FOUND, "El id del usuario registrador no existe");

            
            // Actualiza en familia
        familia.setIdUsuarioRegistrador(in.getIdUsuarioRegistrador());
        familia.setFotoIdentificacion(usuario.getFotoIdentificacion());
        
        if (!familia.getEstado().equalsIgnoreCase("APROBADO"))
            familia.setEstado("APROBADO");
        
        // Actualiza el estado del usuario a APROBADO si no lo está
        if (!usuario.getEstado().equalsIgnoreCase("APROBADO")) {
            usuario.setEstado("APROBADO");

            DtoQrUsuarioIn qrIn =  new DtoQrUsuarioIn();
            qrIn.setIdUsuario(usuario.getIdUsuario());

            QR qr = mapperQr.fromDtoQrInToQrResidente(qrIn);
            
            qrRepository.save(qr);
            usuarioRepository.save(usuario);
        }
        
        familiaRepository.save(familia);



        return new ResponseEntity<>(new ApiResponse("Usuario registrador actualizado correctamente"), HttpStatus.OK);
    } catch (DataAccessException e) {
        if (e.getLocalizedMessage().contains("fk_familia_id_usuario_registrador"))
            throw new ApiException(HttpStatus.NOT_FOUND, "El id del usuario registrador no existe");

        throw new DBAccessException(e);
    }
}



    private Familia validateId(Long id) {
        Familia familia = familiaRepository.findById(id).orElse(null);

        if (familia == null)
            throw new ApiException(HttpStatus.NOT_FOUND, "Familia no encontrada con id: " + id);


        return familia;
    }
}
