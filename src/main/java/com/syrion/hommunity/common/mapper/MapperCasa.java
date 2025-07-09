package com.syrion.hommunity.common.mapper;

import org.springframework.stereotype.Service;

import com.syrion.hommunity.api.dto.in.DtoCasaIn;
import com.syrion.hommunity.api.entity.Casa;

@Service
public class MapperCasa {
    
    public Casa fromDtoCasaInToCasa(DtoCasaIn in) {
        Casa casa = new Casa();
        casa.setNumero(in.getNumero());
        casa.setCalle(in.getCalle());
        casa.setIdZona(in.getIdZona());
        return casa;
    }
}
