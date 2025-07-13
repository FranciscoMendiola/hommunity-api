package com.syrion.hommunity.api.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DtoQrScanIn {

    @JsonProperty("codigo")
    @NotNull(message = "El codigo es obligatorio")
    @NotBlank(message = "El codigo no puede estar en blanco")
    private String codigo;
}
