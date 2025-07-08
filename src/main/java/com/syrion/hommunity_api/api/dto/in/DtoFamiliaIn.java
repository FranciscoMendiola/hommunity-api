package com.syrion.hommunity_api.api.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DtoFamiliaIn {

    @JsonProperty("apellido")
    @NotNull(message = "El apellido es obligatorio")
    @NotBlank(message = "El apellido no puede estar en blanco")
    private String apellido;

    @JsonProperty("idCasa")
    @NotNull(message = "La casa es obligatoria")
    private Long idCasa;

    @JsonProperty("idUsuarioRegistrador")
    private Long idUsuarioRegistrador;   
}
