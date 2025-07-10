package com.syrion.hommunity.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.syrion.hommunity.api.dto.in.DtoQrInvitadoIn;
import com.syrion.hommunity.api.dto.in.DtoQrResidenteIn;
import com.syrion.hommunity.api.entity.QR;
import com.syrion.hommunity.common.dto.ApiResponse;

public interface SvcQr {

    public ResponseEntity<QR> getCodigo(Long id);
    public ResponseEntity<List<QR>> getCodigos();
    public ResponseEntity<List<QR>> getCodigosActivos();
    public ResponseEntity<ApiResponse> createCodigoInvitado(DtoQrInvitadoIn in);
    public ResponseEntity<ApiResponse> createCodigoResidente(DtoQrResidenteIn in);
    public ResponseEntity<ApiResponse> validar(Long id);
    public ResponseEntity<String> getCodigoUsuario(Long idUsuario);
    public ResponseEntity<String> getCodigoQrPorInvitado(Long idInvitado);

}
