package com.syrion.hommunity_api.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.syrion.hommunity_api.api.dto.in.DtoInvitadoIn;
import com.syrion.hommunity_api.api.dto.out.DtoInvitadoOut;
import com.syrion.hommunity_api.common.dto.ApiResponse;

public interface SvcInvitado {

    public ResponseEntity<List<DtoInvitadoOut>> getInvitados();
    public ResponseEntity<DtoInvitadoOut> getInvitado(Long id);
    public ResponseEntity<ApiResponse> createInvitado(DtoInvitadoIn in);
}
