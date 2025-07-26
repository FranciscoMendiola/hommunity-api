package com.syrion.hommunity.api.dto.out;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoQRInfoOut {
    private String codigo;
    private Long idUsuario;
    private Long idInvitado;
    private Integer usosDisponibles;
    private Boolean vigente;
}

