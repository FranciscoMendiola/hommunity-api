package com.syrion.hommunity.api.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoEstadoUsuariIn {
    
    @JsonProperty("estado")
    @NotNull(message = "El estado es obligatorio")
    @NotBlank(message = "El estado no puede estar en blanco")
    private String estado;
}
