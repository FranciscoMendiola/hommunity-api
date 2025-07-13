package com.syrion.hommunity.api.service;

import org.springframework.http.ResponseEntity;

import com.syrion.hommunity.api.dto.in.DtoQrInvitadoIn;
import com.syrion.hommunity.api.dto.in.DtoQrUsuarioIn;
import com.syrion.hommunity.api.dto.out.DtoQrInvitadoOut;
import com.syrion.hommunity.api.dto.out.DtoQrUsuarioOut;
import com.syrion.hommunity.api.entity.QR;
import com.syrion.hommunity.common.dto.ApiResponse;

public interface SvcQr {

    public ResponseEntity<DtoQrUsuarioOut> getCodigoUsuario(Long idUsuario);
    public ResponseEntity<DtoQrInvitadoOut> getCodigoInvitado(Long id);
    public ResponseEntity<ApiResponse> createCodigoUsuario(DtoQrUsuarioIn in);
    public ResponseEntity<DtoQrInvitadoOut> createCodigoInvitado(DtoQrInvitadoIn in);
    public ResponseEntity<ApiResponse> scanQr(String codigo);
}
