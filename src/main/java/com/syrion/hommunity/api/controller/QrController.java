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

import com.syrion.hommunity.api.dto.in.DtoQrInvitadoIn;
import com.syrion.hommunity.api.dto.in.DtoQrResidenteIn;
import com.syrion.hommunity.api.entity.QR;
import com.syrion.hommunity.api.service.SvcQr;
import com.syrion.hommunity.common.dto.ApiResponse;
import com.syrion.hommunity.exception.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/qr")
@Tag(name = "QR", description = "Gestión de códigos QR")
public class QrController {

    @Autowired
    private SvcQr svc;

    @GetMapping
    @Operation(summary = "Obtener lista de códigos QR", description = "Permite obtener una lista de todos los códigos QR registrados en el sistema.")
    public ResponseEntity<List<QR>> getCodigos() {
        return svc.getCodigos();
    }

    @GetMapping("/active")
    @Operation(summary = "Obtener códigos QR activos", description = "Permite obtener una lista de códigos QR que están activos en el sistema.")
    public ResponseEntity<List<QR>> getCodigosActivos() {
        return svc.getCodigosActivos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener código QR por ID", description = "Permite obtener los detalles de un código QR específico por su ID.")
    public ResponseEntity<QR> getCodigo(@Valid @PathVariable("id") Long id) {
        return svc.getCodigo(id);   
    }

    @GetMapping("/invitado/{idInvitado}/codigo")
    @Operation(summary = "Obtener código QR por idInvitado", description = "Obtiene el código QR registrado para un invitado.")
    public ResponseEntity<String> getCodigoQrPorInvitado(@PathVariable Long idInvitado) {
        return svc.getCodigoQrPorInvitado(idInvitado);
    }

    @PostMapping("/invitado")
    @Operation(summary = "Crear código QR", description = "Permite crear un nuevo código QR en el sistema.")
    public ResponseEntity<ApiResponse> createCodigoInvitado(@Valid @RequestBody DtoQrInvitadoIn in, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";
            throw new ApiException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return svc.createCodigoInvitado(in);
    }

    @PostMapping("/{id}/validate")
    @Operation(summary = "Validar código QR", description = "Permite validar y usar (por única vez) un código QR por su ID.")
    public ResponseEntity<ApiResponse> validar(@Valid @PathVariable("id") Long id) {
        return svc.validar(id);
    }

    @PostMapping("/residente")
    @Operation(summary = "Crear código QR para residente", description = "Permite crear un código QR para un residente con vigencia de acceso prolongada.")
    public ResponseEntity<ApiResponse> createCodigoResidente(@Valid @RequestBody DtoQrResidenteIn in, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";
            throw new ApiException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return svc.createCodigoResidente(in);
    }

    @GetMapping("/residente/{idUsuario}")
    @Operation(summary = "Obtener código QR de residente", description = "Devuelve solo el valor del código QR asignado a un usuario residente.")
    public ResponseEntity<String> getCodigoDeResidente(@PathVariable Long idUsuario) {
        return svc.getCodigoUsuario(idUsuario);
    }

}

