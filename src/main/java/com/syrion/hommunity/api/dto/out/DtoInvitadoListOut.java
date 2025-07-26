package com.syrion.hommunity.api.dto.out;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoInvitadoListOut {
    private Long idInvitado;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private boolean vigente;
}