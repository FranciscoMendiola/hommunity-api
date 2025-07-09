package com.syrion.hommunity.api.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoAuthIn {

    @JsonProperty("correo")
    @NotNull(message = "El correo es obligatorio")
    @NotBlank(message = "El correo no puede estar en blanco")
    private String correo;

    @JsonProperty("contraseña")
    @NotNull(message = "La contraseña es obligatoria")
    @NotBlank(message = "La contraseña no puede estar en blanco")
    private String contraseña;
}
