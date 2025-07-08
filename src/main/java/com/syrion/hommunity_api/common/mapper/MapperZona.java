package com.syrion.hommunity_api.common.mapper;

import org.springframework.stereotype.Service;

import com.syrion.hommunity_api.api.dto.in.DtoZonaIn;
import com.syrion.hommunity_api.api.entity.Zona;

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

