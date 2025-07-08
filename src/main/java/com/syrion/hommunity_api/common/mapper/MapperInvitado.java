package com.syrion.hommunity_api.common.mapper;

import org.springframework.stereotype.Service;

import com.syrion.hommunity_api.api.dto.in.DtoInvitadoIn;
import com.syrion.hommunity_api.api.entity.Invitado;



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
