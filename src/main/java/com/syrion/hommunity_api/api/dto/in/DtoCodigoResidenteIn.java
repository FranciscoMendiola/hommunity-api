package com.syrion.hommunity_api.api.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCodigoResidenteIn {

    @JsonProperty("idUsuario")
    @NotNull(message = "El id del usuario residente es obligatorio")
    private Long idUsuario;

}
