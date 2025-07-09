package com.syrion.hommunity.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.syrion.hommunity.api.dto.in.DtoCasaIn;
import com.syrion.hommunity.api.entity.Casa;
import com.syrion.hommunity.api.repository.CasaRepository;
import com.syrion.hommunity.api.repository.ZonaRepository;
import com.syrion.hommunity.common.dto.ApiResponse;
import com.syrion.hommunity.common.mapper.MapperCasa;
import com.syrion.hommunity.exception.ApiException;
import com.syrion.hommunity.exception.DBAccessException;

@Service
public class SvcCasaImp implements SvcCasa {

    @Autowired
    private CasaRepository casaRepository;

    @Autowired
    private ZonaRepository zonaRepository;

    @Autowired
    private MapperCasa mapper;

    @Override
    public ResponseEntity<List<Casa>> getCasasPorZona(Long idZona) {
        try {
            if (!zonaRepository.existsById(idZona))
                throw new ApiException(HttpStatus.NOT_FOUND, "El id de la zona no esta registrado");

            List<Casa> casas = casaRepository.findByIdZona(idZona);

            return new ResponseEntity<>(casas, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<Casa> getCasaPorId(Long id) {
        try {
            Casa casa = validateId(id);
            
            return new ResponseEntity<>(casa, HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<Casa> createCasa(DtoCasaIn casaIn) {
        try {
            Casa casa = mapper.fromDtoCasaInToCasa(casaIn);

            casaRepository.save(casa);

            return new ResponseEntity<>(casa, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            if (e.getLocalizedMessage().contains("ux_casa_id_zona_calle_numero"))
                throw new ApiException(HttpStatus.CONFLICT, "La casa ya está registrado");

            if (e.getLocalizedMessage().contains("fk_casa_id_zona"))
                throw new ApiException(HttpStatus.NOT_FOUND, "El id de la zona no esta registrado");

            throw new DBAccessException(e);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> deleteCasa(Long idCasa) {
        try {
            validateId(idCasa);
            casaRepository.deleteById(idCasa);
            
            return new ResponseEntity<>(new ApiResponse("Casa eliminada correctamente"), HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    public Casa validateId(Long id) {
        Casa casa = casaRepository.findById(id).orElse(null);

        if (casa == null)
            throw new ApiException(HttpStatus.NOT_FOUND, "El id de la casa no esta registrado");

        return casa;
    }
}