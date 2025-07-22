package com.syrion.hommunity.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import com.syrion.hommunity.api.dto.in.DtoQrInvitadoIn;
import com.syrion.hommunity.api.dto.in.DtoQrUsuarioIn;
import com.syrion.hommunity.api.dto.out.DtoQrInvitadoOut;
import com.syrion.hommunity.api.dto.out.DtoQrUsuarioOut;
import com.syrion.hommunity.api.dto.out.DtoQrUsuarioCodigoOut;
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

    @GetMapping("/invitado/{idInvitado}")
    @Operation(summary = "Obtener código QR por idInvitado", description = "Obtiene el código QR registrado para un invitado.")
    public ResponseEntity<DtoQrInvitadoOut> getCodigoInvitado(@PathVariable Long idInvitado) {
        return svc.getCodigoInvitado(idInvitado);
    }

    @PostMapping("/invitado")
    @Operation(summary = "Crear código QR", description = "Permite crear un nuevo código QR en el sistema.")
    public ResponseEntity<DtoQrInvitadoOut> createCodigoInvitado(@Valid @RequestBody DtoQrInvitadoIn in, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";
            throw new ApiException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return svc.createCodigoInvitado(in);
    }

    @GetMapping("{codigo}/scan")
    @Operation(summary = "Escanear y validar código QR", description = "Permite validar un código QR escaneado por el código directamente.")
    public ResponseEntity<ApiResponse> scanQr(
            @PathVariable String codigo) {
        return svc.scanQr(codigo);
    }

    @PostMapping("/residente")
    @Operation(summary = "Crear código QR para residente", description = "Permite crear un código QR para un residente con vigencia de acceso prolongada.")
    public ResponseEntity<ApiResponse> createCodigoResidente(@Valid @RequestBody DtoQrUsuarioIn in, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";
            throw new ApiException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return svc.createCodigoUsuario(in);
    }

    // Endpoint que devuelve toda la info del QR (incluye imagen en bytes)
    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Obtener código QR de residente", description = "Devuelve el objeto completo del código QR asignado a un usuario residente.")
    public ResponseEntity<DtoQrUsuarioOut> getCodigoUsuario(@PathVariable Long idUsuario) {
        return svc.getCodigoUsuario(idUsuario);
    }

    // Nuevo endpoint que devuelve solo el código QR como texto plano, usando nuevo DTO simple
    @GetMapping("/residente/{idUsuario}/codigo")
    @Operation(summary = "Obtener solo código QR de residente", description = "Devuelve solo el valor del código QR asignado a un usuario residente en texto plano.")
    public ResponseEntity<DtoQrUsuarioCodigoOut> getCodigoUsuarioSimple(@PathVariable Long idUsuario) {
        return svc.getCodigoUsuarioSimple(idUsuario);
    }
}
