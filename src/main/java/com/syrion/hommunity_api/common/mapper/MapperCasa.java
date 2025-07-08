package com.syrion.hommunity_api.common.mapper;

import org.springframework.stereotype.Service;

import com.syrion.hommunity_api.api.dto.in.DtoCasaIn;
import com.syrion.hommunity_api.api.entity.Casa;

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
