package com.syrion.hommunity_api.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syrion.hommunity_api.api.dto.in.DtoCasaIn;
import com.syrion.hommunity_api.api.entity.Casa;
import com.syrion.hommunity_api.api.service.SvcCasa;
import com.syrion.hommunity_api.common.dto.ApiResponse;
import com.syrion.hommunity_api.exception.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/casa")
@Tag(name = "Casa", description = "Gestión de casas")
public class CasaController {

    @Autowired
    private SvcCasa svcCasa;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Administrador', 'Residente')")
    @Operation(summary = "Obtener casa por ID", description = "Permite obtener los detalles de una casa específica por su ID.")
    public ResponseEntity<Casa> obtenerCasaPorId(@Valid @PathVariable("id") Long id) {
        return svcCasa.getCasaPorId(id);
    }

    @GetMapping("/zona/{idZona}")
    @PreAuthorize("hasAnyAuthority('Administrador', 'Residente')")
    @Operation(summary = "Obtener casas por zona", description = "Permite obtener una lista de casas que pertenecen a una zona específica.")
    public ResponseEntity<List<Casa>> obtenerCasasPorZona(@Valid @PathVariable("idZona") Long idZona) {
        return svcCasa.getCasasPorZona(idZona);
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('Administrador', 'Residente')")
    @Operation(summary = "Crear casa", description = "Permite crear una nueva casa en el sistema.")
    public ResponseEntity<Casa> crearCasa(@Valid @RequestBody DtoCasaIn casaIn, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";
            throw new ApiException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return svcCasa.createCasa(casaIn);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Administrador', 'Residente')")
    @Operation(summary = "Eliminar casa", description = "Permite eliminar una casa del sistema por su ID.")
    public ResponseEntity<ApiResponse> eliminarCasa(@Valid @PathVariable("id") Long id) {
        return svcCasa.deleteCasa(id);
    }
    
}