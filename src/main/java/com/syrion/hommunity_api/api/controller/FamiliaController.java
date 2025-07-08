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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syrion.hommunity_api.api.dto.in.DtoFamiliaIn;
import com.syrion.hommunity_api.api.dto.in.DtoUsuarioRegistradorIn;
import com.syrion.hommunity_api.api.entity.Familia;
import com.syrion.hommunity_api.api.service.SvcFamilia;
import com.syrion.hommunity_api.common.dto.ApiResponse;
import com.syrion.hommunity_api.exception.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/familia")
@Tag(name = "Familia", description = "Gestión de familias")
public class FamiliaController {

    @Autowired
    private SvcFamilia svcFamilia;
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Administrador', 'Residente')")
    @Operation(summary = "Obtener familia por ID", description = "Permite obtener los detalles de una familia específica por su ID.")
    public ResponseEntity<Familia> obtenerFamiliaPorId(@Valid @PathVariable("id") Long id) {
        return svcFamilia.getFamiliaPorId(id);
    }
    
    @GetMapping("/zona/{idZona}")
    @Operation(summary = "Obtener familias por zona", description = "Permite obtener una lista de familias que pertenecen a una zona específica.")
    public ResponseEntity<List<Familia>> obtenerFamiliasPorZona(@Valid @PathVariable("idZona") Long idZona) {
        return svcFamilia.getFamiliasPorZona(idZona);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('Administrador', 'Residente')")
    @Operation(summary = "Crear familia", description = "Permite crear una nueva familia en el sistema.")
    public ResponseEntity<ApiResponse> createFamilia(@Valid @RequestBody DtoFamiliaIn familiaIn, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";
            throw new ApiException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return svcFamilia.createFamilia(familiaIn);
    }
    
    @PatchMapping("{id}/registrador")
    @Operation(summary = "Actualizar usuario registrador", description = "Permite actualizar el usuario registrador de una familia por su ID.")
    public ResponseEntity<ApiResponse> updateUsuarioRegistrador(@Valid @PathVariable("id") Long idFamilia, @Valid @RequestBody DtoUsuarioRegistradorIn in, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";
            throw new ApiException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return svcFamilia.updateUsuarioRegistrador(idFamilia, in);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('Administrador', 'Residente')")
    @Operation(summary = "Eliminar familia", description = "Permite eliminar una familia del sistema por su ID.")
    public ResponseEntity<ApiResponse> deleteFamilia(@Valid @PathVariable("id") Long id) {
        return svcFamilia.deleteFamilia(id);
    }
}
