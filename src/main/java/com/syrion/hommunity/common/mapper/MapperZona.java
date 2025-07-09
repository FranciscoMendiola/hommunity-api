package com.syrion.hommunity.common.mapper;

import org.springframework.stereotype.Service;

import com.syrion.hommunity.api.dto.in.DtoZonaIn;
import com.syrion.hommunity.api.entity.Zona;

@Service
public class MapperZona {
    
    
    public Zona fromDtoZonaInToZona(DtoZonaIn in) {
         Zona zona = new Zona();
         zona.setNombre(in.getNombre());
         zona.setCodigoPostal(in.getCodigoPostal());
         zona.setColonia(in.getColonia());
         zona.setMunicipio(in.getMunicipio());

         return zona;
     }
}

