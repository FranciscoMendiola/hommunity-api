package com.syrion.hommunity_api.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.syrion.hommunity_api.api.dto.in.DtoEstadoUsuariIn;
import com.syrion.hommunity_api.api.dto.in.DtoUsuarioContraseñaIn;
import com.syrion.hommunity_api.api.dto.in.DtoUsuarioIn;
import com.syrion.hommunity_api.api.dto.out.DtoUsuarioOut;
import com.syrion.hommunity_api.api.service.SvcUsuario;
import com.syrion.hommunity_api.common.dto.ApiResponse;
import com.syrion.hommunity_api.exception.ApiException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuario", description = "Gestión de usuarios")
public class UsuarioController {

    @Autowired
    SvcUsuario svUsuario;

    // Obtener usuario por ID
    @GetMapping("/{idUsuario}")
    @Operation(summary = "Obtener usuario por ID", description = "Permite obtener los detalles de un usuario específico por su ID.")
    public ResponseEntity<DtoUsuarioOut> getUsuarioPorId(@Valid @PathVariable("idUsuario") Long idUsuario) {
        return svUsuario.getUsuario(idUsuario);
    }

    // Obtener usuarios por zona
    @GetMapping("/zona/{idZona}")
    @Operation(summary = "Obtener usuarios por zona", description = "Permite obtener una lista de usuarios que pertenecen a una zona específica.")
    public ResponseEntity<List<DtoUsuarioOut>> getUsuariosPorZona(@Valid @PathVariable("idZona") Long idZona) {
        return svUsuario.getUsuariosPorZona(idZona);
    }

    // Obtener usuarios por familia
    @GetMapping("/familia/{idFamilia}")
    @Operation(summary = "Obtener usuarios por familia", description = "Permite obtener una lista de usuarios que pertenecen a una familia específica.")
    public ResponseEntity<List<DtoUsuarioOut>> getUsuariosPorFamilia(@Valid @PathVariable("idFamilia") Long idFamilia) {
        return svUsuario.getUsuariosPorFamilia(idFamilia);
    }

    // Crear usuario
    @PostMapping
    @Operation(summary = "Crear usuario", description = "Permite crear un nuevo usuario en el sistema.")
    public ResponseEntity<ApiResponse> createUsuario(@Valid @RequestBody DtoUsuarioIn in, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";
            throw new ApiException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        return svUsuario.createUsuario(in);
    }

    // Eliminar usuario
    @DeleteMapping("/{idUsuario}/delete")
    @Operation(summary = "Eliminar usuario", description = "Permite eliminar un usuario del sistema por su ID.")
    public ResponseEntity<ApiResponse> deleteUsuario(@Valid @PathVariable("idUsuario") Long idUsuario) {
        return svUsuario.deleteUsuario(idUsuario);
    }

    // Actualizar estado de usuario
    @PatchMapping("/{idUsuario}/estado")
    @Operation(summary = "Actualizar estado de usuario", description = "Permite actualizar el estado de un usuario (aprobado/pendiente) por su ID.")
    public ResponseEntity<ApiResponse> updateEstadoUsuario(@Valid @PathVariable("idUsuario") Long idUsuario, 
                                                            @Valid @RequestBody DtoEstadoUsuariIn in, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";
            throw new ApiException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        return svUsuario.updateEstadoUsuario(idUsuario, in);                                        
    }

    // Actualizar contraseña de usuario
    @PatchMapping("/{idUsuario}/contraseña")
    @Operation(summary = "Actualizar contraseña de usuario", description = "Permite actualizar la contraseña de un usuario por su ID.")
    public ResponseEntity<ApiResponse> updateContraseña(@Valid @PathVariable("idUsuario") Long idUsuario, 
                                                        @Valid @RequestBody DtoUsuarioContraseñaIn in, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            FieldError fieldError = bindingResult.getFieldError();
            String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error";
            throw new ApiException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        
        return svUsuario.updateContraseña(idUsuario, in);
    }
}
