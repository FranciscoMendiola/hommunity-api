package com.syrion.hommunity_api.common.mapper;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.syrion.hommunity_api.api.dto.in.DtoInvitadoIn;
import com.syrion.hommunity_api.api.dto.out.DtoInvitadoOut;
import com.syrion.hommunity_api.api.entity.Invitado;



@Service
public class MapperInvitado {

    public Invitado fromInvitado(DtoInvitadoIn in) {
        Invitado invitado = new Invitado();
        invitado.setNombre(in.getNombre());
        invitado.setApellidoPaterno(in.getApellidoPaterno());
        invitado.setApellidoMaterno(in.getApellidoMaterno());
        invitado.setFechaEntrada(in.getFechaEntrada());
        invitado.setFechaSalida(in.getFechaSalida());


        return invitado;
    }


    public DtoInvitadoOut fromInvitado(Invitado invitado) {
        DtoInvitadoOut out = new DtoInvitadoOut();
        out.setIdInvitado(invitado.getIdInvitado());
        out.setNombre(invitado.getNombre());
        out.setApellidoPaterno(invitado.getApellidoPaterno());
        out.setApellidoMaterno(invitado.getApellidoMaterno());
        out.setFechaEntrada(invitado.getFechaEntrada());
        out.setFechaSalida(invitado.getFechaSalida());
        out.setIdUsuario(invitado.getIdUsuario().getIdUsuario());

        return out;
    }

    public List<DtoInvitadoOut> fromInvitados(List<Invitado> invitados) {
        List<DtoInvitadoOut> outList = new ArrayList<>();
        
        for (Invitado invitado : invitados) {
            DtoInvitadoOut out = fromInvitado(invitado);
            outList.add(out);
        }
        return outList;
    }
}
