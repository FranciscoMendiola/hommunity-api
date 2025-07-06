package com.syrion.hommunity_api.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.syrion.hommunity_api.api.dto.in.DtoQrInvitadoIn;
import com.syrion.hommunity_api.api.dto.in.DtoQrResidenteIn;
import com.syrion.hommunity_api.api.entity.QR;
import com.syrion.hommunity_api.common.dto.ApiResponse;

public interface SvcQr {

    public ResponseEntity<QR> getCodigo(Long id);
    public ResponseEntity<List<QR>> getCodigos();
    public ResponseEntity<List<QR>> getCodigosActivos();
    public ResponseEntity<ApiResponse> createCodigoInvitado(DtoQrInvitadoIn in);
    public ResponseEntity<ApiResponse> createCodigoResidente(DtoQrResidenteIn in);
    public ResponseEntity<ApiResponse> validar(Long id);

}
