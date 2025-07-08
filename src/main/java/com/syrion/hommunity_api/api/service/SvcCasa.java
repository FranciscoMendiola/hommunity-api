package com.syrion.hommunity_api.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.syrion.hommunity_api.api.dto.in.DtoCasaIn;
import com.syrion.hommunity_api.api.entity.Casa;
import com.syrion.hommunity_api.common.dto.ApiResponse;


public interface SvcCasa {
    ResponseEntity<Casa> createCasa(DtoCasaIn casaIn);
    ResponseEntity<ApiResponse> deleteCasa(Long idCasa);
    ResponseEntity<List<Casa>> getCasasPorZona(Long idZona);
    ResponseEntity<Casa> getCasaPorId(Long id);
}
