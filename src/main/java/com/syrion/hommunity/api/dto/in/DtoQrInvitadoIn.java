package com.syrion.hommunity.api.dto.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoQrInvitadoIn {

    @JsonProperty("idInvitado")
    @NotNull(message = "El id del invitado es obligatorio")
    private Long idInvitado;

    @JsonProperty("usosDisponibles")
    @NotNull(message = "El numero de usos es obligatorio")
    @Min(value = 1, message = "El número de usos debe ser al menos 1 (Un uso es entrada y salida).")
    private Integer usosDisponibles;
}
