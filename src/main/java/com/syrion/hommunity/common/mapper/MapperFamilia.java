package com.syrion.hommunity.common.mapper;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.syrion.hommunity.api.dto.in.DtoFamiliaIn;
import com.syrion.hommunity.api.entity.Familia;
import com.syrion.hommunity.api.entity.Usuario;
import com.syrion.hommunity.api.repository.UsuarioRepository;
import com.syrion.hommunity.exception.ApiException;
import com.syrion.hommunity.exception.DBAccessException;

@Service
public class MapperFamilia {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Familia fromDtoFamiliaInTo(DtoFamiliaIn in) {
        Familia familia = new Familia();
        familia.setApellido(in.getApellido());
        familia.setFechaRegistro(LocalDateTime.now());
        familia.setIdCasa(in.getIdCasa());
        if (in.getIdUsuarioRegistrador() != null) {
            try {
                Usuario usuario = usuarioRepository.findById(in.getIdUsuarioRegistrador()).orElse(null);

                if (usuario == null)
                    throw new ApiException(HttpStatus.NOT_FOUND, "El id del usuario registrador no existe");

                familia.setIdUsuarioRegistrador(in.getIdUsuarioRegistrador());
                familia.setFotoIdentificacion(usuario.getFotoIdentificacion());
                familia.setEstado("APROBADO");
            } catch (DataAccessException e) {
                throw new DBAccessException(e);
            }
        } else {
            familia.setEstado("PENDIENTE");
        }

        return familia;
    }
}

