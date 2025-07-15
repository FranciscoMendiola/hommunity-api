package com.syrion.hommunity.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.syrion.hommunity.api.dto.in.DtoZonaIn;
import com.syrion.hommunity.api.entity.Zona;
import com.syrion.hommunity.api.repository.ZonaRepository;
import com.syrion.hommunity.common.dto.ApiResponse;
import com.syrion.hommunity.common.mapper.MapperZona;
import com.syrion.hommunity.exception.ApiException;
import com.syrion.hommunity.exception.DBAccessException;

@Service
public class SvcZonaImp implements SvcZona {

    @Autowired
    private ZonaRepository zonaRepository;

    @Autowired
    private MapperZona mapper;

    @Override
    public ResponseEntity<ApiResponse> createZona(DtoZonaIn in) {
        try {
            Zona zona = mapper.fromDtoZonaInToZona(in);

            zonaRepository.save(zona);
            return new ResponseEntity<>(new ApiResponse("Zona creada correctamente"), HttpStatus.CREATED);
        } catch (DataAccessException e) {
            if (e.getLocalizedMessage().contains("ux_zona_municipio_cp_colonia_nombre"))
                throw new ApiException(HttpStatus.CONFLICT, "La zona ya esta registrada");

            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<List<Zona>> getZonas() {
        try {
            List<Zona> zonas = zonaRepository.findAll();
            return new ResponseEntity<>(zonas, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<Zona> getZonaById(Long id) {
        try {
            Zona zona = zonaRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Zona no encontrada con id: " + id));
            return new ResponseEntity<>(zona, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    
}