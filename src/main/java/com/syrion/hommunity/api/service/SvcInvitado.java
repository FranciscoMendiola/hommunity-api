package com.syrion.hommunity.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.syrion.hommunity.api.dto.in.DtoInvitadoIn;
import com.syrion.hommunity.api.dto.out.DtoQrInvitadoOut;
import com.syrion.hommunity.api.entity.Invitado;
import com.syrion.hommunity.common.dto.ApiResponse;

public interface SvcInvitado {

    public ResponseEntity<List<Invitado>> getInvitados(Long idUsuario);
    public ResponseEntity<Invitado> getInvitado(Long id);
    public ResponseEntity<DtoQrInvitadoOut> getInvitadoDetailsByFamily(Long idUsuario, Long id);
    public ResponseEntity<ApiResponse> createInvitado(DtoInvitadoIn in);
    public ResponseEntity<ApiResponse> deleteInvitadoByFamily(Long idUsuario, Long id);
}