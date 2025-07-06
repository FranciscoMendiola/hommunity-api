package com.syrion.hommunity_api.api.dto.out;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoInvitadoOut {

    @JsonProperty("idInvitado")
    private Long idInvitado;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellidoPaterno")
    private String apellidoPaterno;

    @JsonProperty("apellidoMaterno")
    private String apellidoMaterno;

    @JsonProperty("fechaEntrada")
    private LocalDateTime fechaEntrada;
    
    @JsonProperty("fechaSalida")
    private LocalDateTime fechaSalida;

    @JsonProperty("idUsuario")
    private Long idUsuario;
}
