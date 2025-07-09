package com.syrion.hommunity.api.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoUsuarioContraseñaIn {

    @JsonProperty("contraseñaActual")
    @NotNull(message = "La contraseña actual es obligatoria")
    @NotBlank(message = "La contraseña actual no puede estar en blanco")
    private String contraseñaActual;

    @JsonProperty("nuevaContraseña")
    @NotNull(message = "La nueva contraseña es obligatoria")
    @NotBlank(message = "La nueva contraseña no puede estar en blanco")
    private String nuevaContraseña;
}
