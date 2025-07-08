package com.syrion.hommunity_api.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.syrion.hommunity_api.api.dto.in.DtoFamiliaIn;
import com.syrion.hommunity_api.api.dto.in.DtoUsuarioRegistradorIn;
import com.syrion.hommunity_api.api.entity.Familia;
import com.syrion.hommunity_api.common.dto.ApiResponse;

public interface SvcFamilia {
    ResponseEntity<Familia> getFamiliaPorId(Long id);
    ResponseEntity<List<Familia>> getFamiliasPorZona(Long idZona);
    ResponseEntity<ApiResponse> createFamilia(DtoFamiliaIn familiaIn);
    ResponseEntity<ApiResponse> deleteFamilia(Long idFamilia);
    ResponseEntity<ApiResponse> updateUsuarioRegistrador(Long idFamilia, DtoUsuarioRegistradorIn in);
}
