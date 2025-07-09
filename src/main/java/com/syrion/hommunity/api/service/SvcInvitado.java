package com.syrion.hommunity.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.syrion.hommunity.api.dto.in.DtoInvitadoIn;
import com.syrion.hommunity.api.entity.Invitado;
import com.syrion.hommunity.common.dto.ApiResponse;

public interface SvcInvitado {

    public ResponseEntity<List<Invitado>> getInvitados();
    public ResponseEntity<Invitado> getInvitado(Long id);
    public ResponseEntity<ApiResponse> createInvitado(DtoInvitadoIn in);
}
