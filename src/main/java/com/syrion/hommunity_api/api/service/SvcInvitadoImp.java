package com.syrion.hommunity_api.api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.syrion.hommunity_api.api.dto.in.DtoInvitadoIn;
import com.syrion.hommunity_api.api.dto.out.DtoInvitadoOut;
import com.syrion.hommunity_api.api.entity.Invitado;
import com.syrion.hommunity_api.api.entity.Usuario;
import com.syrion.hommunity_api.api.repository.InvitadoRepository;
import com.syrion.hommunity_api.api.repository.UsuarioRepository;
import com.syrion.hommunity_api.common.dto.ApiResponse;
import com.syrion.hommunity_api.common.mapper.MapperInvitado;
import com.syrion.hommunity_api.exception.ApiException;
import com.syrion.hommunity_api.exception.DBAccessException;

@Service
public class SvcInvitadoImp implements SvcInvitado {

    @Autowired
    private InvitadoRepository repoInvitado;

    @Autowired
    private MapperInvitado mapperInvitado;

    @Autowired
    private UsuarioRepository repoUsuario;

    @Override
    public ResponseEntity<List<DtoInvitadoOut>> getInvitados() {
        try {
            List<Invitado> invitados = repoInvitado.findAll();

            List<DtoInvitadoOut> dtoInvitados = mapperInvitado.fromInvitados(invitados);

            return new ResponseEntity<>(dtoInvitados, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<DtoInvitadoOut> getInvitado(Long id) {
        try {
            Invitado invitado = validateId(id);

            DtoInvitadoOut dtoInvitado = mapperInvitado.fromInvitado(invitado);

            return new ResponseEntity<>(dtoInvitado, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> createInvitado(DtoInvitadoIn in) {
        try {
            // Validar que la fecha de entrada no sea anterior a la fecha/hora actual
            if (in.getFechaEntrada().isBefore(LocalDateTime.now())) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "La fecha de entrada no puede ser anterior a la fecha actual.");
            }

            // Validar que la fecha de salida sea posterior a la fecha de entrada
            if (in.getFechaSalida().isBefore(in.getFechaEntrada())) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "La fecha de salida no puede ser anterior a la fecha de entrada.");
            }

            Usuario usuario = repoUsuario.findById(in.getIdUsuario()).orElse(null);

            if (usuario == null) {
                throw new ApiException(HttpStatus.NOT_FOUND, "El id del usuario residente no está registrado.");
            }

            Invitado invitado = mapperInvitado.fromInvitado(in);
            invitado.setIdUsuario(usuario);

            repoInvitado.save(invitado);

            return new ResponseEntity<>(new ApiResponse("Invitado creado correctamente"), HttpStatus.CREATED);

        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    private Invitado validateId(Long id) {

        Invitado invitado = repoInvitado.findById(id).orElse(null);

        if (invitado == null)
            throw new ApiException(HttpStatus.NOT_FOUND, "El id del invitado no esta registrado.");

        return invitado;
    }
}
