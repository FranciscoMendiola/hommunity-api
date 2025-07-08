package com.syrion.hommunity_api.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.syrion.hommunity_api.api.dto.in.DtoFamiliaIn;
import com.syrion.hommunity_api.api.dto.in.DtoUsuarioRegistradorIn;
import com.syrion.hommunity_api.api.entity.Familia;
import com.syrion.hommunity_api.api.entity.Usuario;
import com.syrion.hommunity_api.api.repository.FamiliaRepository;
import com.syrion.hommunity_api.api.repository.UsuarioRepository;
import com.syrion.hommunity_api.api.repository.ZonaRepository;
import com.syrion.hommunity_api.common.dto.ApiResponse;
import com.syrion.hommunity_api.common.mapper.MapperFamilia;
import com.syrion.hommunity_api.exception.ApiException;
import com.syrion.hommunity_api.exception.DBAccessException;

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

            List<Familia> familias = familiaRepository.findByIdZona(idZona);

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

            familia.setIdUsuarioRegistrador(in.getIdUsuarioRegistrador());
            familia.setFotoIdentificacion(usuario.getFotoIdentificacion());

            if (!familia.getEstado().toLowerCase().equals("aprobado"))
                familia.setEstado("APROBADO");

            familiaRepository.save(familia);

            return new ResponseEntity<>(new ApiResponse("Usuario registrador actualizado correctamente"), HttpStatus.OK);
        } catch (DataAccessException e) {
            // No ocurrirá darse por la verificación de arriba
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
