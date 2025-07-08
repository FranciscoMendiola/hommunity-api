package com.syrion.hommunity_api.api.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoZonaIn {

    @JsonProperty("nombre")
    @NotNull(message = "El nombre de la zona es obligatorio")
    @NotBlank(message = "El nombre de la zona no puede estar en blanco")
    private String nombre;

    @JsonProperty("codigoPostal")
    @NotNull(message = "El código postal es obligatorio")
    private Integer codigoPostal;

    @JsonProperty("municipio")
    @NotNull(message = "El municipio es obligatorio")
    @NotBlank(message = "El municipio no puede estar en blanco")
    private String municipio;

    @JsonProperty("colonia")
    @NotNull(message = "La colonia es obligatoria")
    @NotBlank(message = "La colonia no puede estar en blanco")
    private String colonia;
}
