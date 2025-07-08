package com.syrion.hommunity_api.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syrion.hommunity_api.api.dto.in.DtoZonaIn;
import com.syrion.hommunity_api.api.entity.Zona;
import com.syrion.hommunity_api.api.service.SvcZona;
import com.syrion.hommunity_api.common.dto.ApiResponse;
import com.syrion.hommunity_api.exception.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/zona")
@Tag(name = "Zona", description = "Gestión de zonas")
public class ZonaController {

    private final SvcZona svcZona;

    public ZonaController(SvcZona svcZona) {
        this.svcZona = svcZona;
    }

    @PostMapping
    @Operation(summary = "Crear zona", description = "Permite crear una nueva zona en el sistema.")
    public ResponseEntity<ApiResponse> crearZona(@Valid @RequestBody DtoZonaIn in, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";
            throw new ApiException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return svcZona.createZona(in);
    }

    @GetMapping
    @Operation(summary = "Listar zonas", description = "Permite obtener una lista de todas las zonas registradas en el sistema.")
    public ResponseEntity<List<Zona>> listarZonas() {
        return svcZona.getZonas();
    }
}