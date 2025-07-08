package com.syrion.hommunity_api.api.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoCasaIn {

    @JsonProperty("numero")
    @NotNull(message = "El número de la casa es obligatorio")
    @NotBlank(message = "El número de casa no puede estar en blanco")
    private String numero;

    @JsonProperty("calle")
    @NotNull(message = "La calle es obligatoria")
    @NotBlank(message = "La calle no puede estar en blanco")
    private String calle;

    @JsonProperty("idZona")
    @NotNull(message = "El id de la zona es obligatorio")
    private Long idZona;
}
