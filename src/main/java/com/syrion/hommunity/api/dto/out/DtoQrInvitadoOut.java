package com.syrion.hommunity.api.dto.out;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoQrInvitadoOut {

    @JsonProperty("idQr")
    private Long idQr;

    @JsonProperty("codigo")
    private String codigo;
    
    @JsonProperty("fechaEntrada")
    private LocalDateTime fechaEntrada;

    @JsonProperty("fechaSalida")
    private LocalDateTime fechaSalida;
    
    @JsonProperty("vigente")
    private Boolean vigente;
    
    @JsonProperty("usosDisponibles")
    private Integer usosDisponibles;

    @JsonProperty("nombreInvitado")
    private String nombreInvitado;

    @JsonProperty("qrImageBytes")
    private byte[] qrImageBytes;
}
