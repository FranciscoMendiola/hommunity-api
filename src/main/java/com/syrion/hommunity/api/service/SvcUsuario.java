package com.syrion.hommunity.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.syrion.hommunity.api.dto.in.DtoEstadoUsuariIn;
import com.syrion.hommunity.api.dto.in.DtoUsuarioContraseñaIn;
import com.syrion.hommunity.api.dto.in.DtoUsuarioIn;
import com.syrion.hommunity.api.dto.out.DtoUsuarioOut;
import com.syrion.hommunity.common.dto.ApiResponse;

public interface SvcUsuario {

    public ResponseEntity<DtoUsuarioOut> getUsuario(Long id);
    public ResponseEntity<List<DtoUsuarioOut>> getUsuariosPorZona(Long idZona);
    public ResponseEntity<List<DtoUsuarioOut>> getUsuariosPorFamilia(Long idFamilia);
    public ResponseEntity<ApiResponse> createUsuario(DtoUsuarioIn in);
    public ResponseEntity<ApiResponse> deleteUsuario(Long id);
    public ResponseEntity<ApiResponse> updateEstadoUsuario(Long id, DtoEstadoUsuariIn in);
    public ResponseEntity<ApiResponse> updateContraseña(Long id, DtoUsuarioContraseñaIn in);
}