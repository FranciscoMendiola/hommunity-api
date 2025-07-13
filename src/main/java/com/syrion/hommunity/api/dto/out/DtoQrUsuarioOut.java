package com.syrion.hommunity.api.dto.out;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoQrUsuarioOut {

    @JsonProperty("idQr")
    private Long idQr;

    @JsonProperty("codigo")
    private String codigo;
    
    @JsonProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;
    
    @JsonProperty("vigente")
    private Boolean vigente;
    
    @JsonProperty("usosDisponibles")
    private Integer usosDisponibles;

    @JsonProperty("idUsuario")
    private Long idUsuario;

    @JsonProperty("qrImageBytes")
    private byte[] qrImageBytes;
}