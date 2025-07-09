package com.syrion.hommunity.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.syrion.hommunity.api.entity.Rol;
import com.syrion.hommunity.api.repository.RolRepository;

@Component
public class ConverterRol {
    
    @Autowired
    private RolRepository rolRepository;

    public Long getIdRol(String nombreRol) {
        return rolRepository.findIdByNombreRol(nombreRol).orElse(-1L);

    }

    public String getNombreRol(Long idRol) {
        return rolRepository.findById(idRol).map(Rol::getNombreRol).orElse("SIN ROL");
    }
}
