package com.syrion.hommunity.common.mapper;

import org.springframework.stereotype.Service;

import com.syrion.hommunity.api.dto.in.DtoInvitadoIn;
import com.syrion.hommunity.api.entity.Invitado;



@Service
public class MapperInvitado {

    public Invitado fromDtoInvitadoInToInvitado(DtoInvitadoIn in) {
        Invitado invitado = new Invitado();
        invitado.setNombre(in.getNombre());
        invitado.setApellidoPaterno(in.getApellidoPaterno());
        invitado.setApellidoMaterno(in.getApellidoMaterno());
        invitado.setFechaEntrada(in.getFechaEntrada());
        invitado.setFechaSalida(in.getFechaSalida());

        return invitado;
    }
}
