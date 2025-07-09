package com.syrion.hommunity.api.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoUsuarioRegistradorIn {

    @JsonProperty("idUsuarioRegistrador")
    @NotNull(message = "El id del usuario registrador es obligatorio")
    private Long idUsuarioRegistrador;
}
