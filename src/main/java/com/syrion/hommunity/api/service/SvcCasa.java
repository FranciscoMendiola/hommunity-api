package com.syrion.hommunity.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.syrion.hommunity.api.dto.in.DtoCasaIn;
import com.syrion.hommunity.api.entity.Casa;
import com.syrion.hommunity.common.dto.ApiResponse;


public interface SvcCasa {
    ResponseEntity<Casa> createCasa(DtoCasaIn casaIn);
    ResponseEntity<ApiResponse> deleteCasa(Long idCasa);
    ResponseEntity<List<Casa>> getCasasPorZona(Long idZona);
    ResponseEntity<Casa> getCasaPorId(Long id);
}
