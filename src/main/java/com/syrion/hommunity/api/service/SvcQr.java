package com.syrion.hommunity.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.syrion.hommunity.api.dto.in.DtoQrInvitadoIn;
import com.syrion.hommunity.api.dto.in.DtoQrResidenteIn;
import com.syrion.hommunity.api.entity.QR;
import com.syrion.hommunity.common.dto.ApiResponse;

public interface SvcQr {

    ResponseEntity<QR> getCodigo(Long id);
    ResponseEntity<List<QR>> getCodigos();
    ResponseEntity<List<QR>> getCodigosActivos();
    ResponseEntity<ApiResponse> createCodigoInvitado(DtoQrInvitadoIn in);
    ResponseEntity<ApiResponse> createCodigoResidente(DtoQrResidenteIn in);
    ResponseEntity<ApiResponse> validar(Long id);
    ResponseEntity<String> getCodigoQrPorInvitado(Long idInvitado);
}