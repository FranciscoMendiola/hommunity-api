package com.syrion.hommunity.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.syrion.hommunity.api.dto.in.DtoZonaIn;
import com.syrion.hommunity.api.entity.Zona;
import com.syrion.hommunity.common.dto.ApiResponse;

public interface SvcZona {
    ResponseEntity<ApiResponse> createZona(DtoZonaIn dto);
    ResponseEntity<List<Zona>> getZonas();
}
