package com.syrion.hommunity.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syrion.hommunity.api.dto.in.DtoInvitadoIn;
import com.syrion.hommunity.api.entity.Invitado;
import com.syrion.hommunity.api.service.SvcInvitado;
import com.syrion.hommunity.common.dto.ApiResponse;
import com.syrion.hommunity.exception.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/invitado")
@Tag (name = "Invitado", description = "Gestión de invitados")
public class InvitadoController {

    @Autowired
    SvcInvitado svc;
    
    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Obtener lista de invitados por usuario", description = "Permite obtener los últimos 5 invitados registrados por el usuario especificado.")
    public ResponseEntity<List<Invitado>> getInvitadosPorUsuario(@PathVariable Long idUsuario) {
        return svc.getInvitados(idUsuario);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener invitado por ID", description = "Permite obtener los detalles de un invitado específico por su ID.")
    public ResponseEntity<Invitado> getInvitado(@Valid @PathVariable("id") Long id) {
        return svc.getInvitado(id);
    }

    @PostMapping
    @Operation(summary = "Crear invitado", description = "Permite crear un nuevo invitado en el sistema.")
    public ResponseEntity<ApiResponse> createInvitado(@Valid @RequestBody DtoInvitadoIn in, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";
            throw new ApiException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return svc.createInvitado(in);
    }
}
