package com.syrion.hommunity.api.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.syrion.hommunity.api.dto.in.DtoEstadoUsuariIn;
import com.syrion.hommunity.api.dto.in.DtoUsuarioContraseñaIn;
import com.syrion.hommunity.api.dto.in.DtoUsuarioIn;
import com.syrion.hommunity.api.dto.out.DtoFamiliaPersonasOut;
import com.syrion.hommunity.api.dto.out.DtoUsuarioOut;
import com.syrion.hommunity.common.dto.ApiResponse;

public interface SvcUsuario {
    ResponseEntity<DtoUsuarioOut> getUsuario(Long idUsuario);
    ResponseEntity<List<DtoUsuarioOut>> getUsuariosPorZona(Long idZona);
    ResponseEntity<List<DtoUsuarioOut>> getUsuariosPorFamilia(Long idFamilia);
    ResponseEntity<List<DtoFamiliaPersonasOut>> getUsuariosAprobadosPorFamilia(Long idFamilia);
    ResponseEntity<List<DtoUsuarioOut>> getUsuariosPendientesAdmin(Long idZona);
    ResponseEntity<List<DtoUsuarioOut>> getUsuariosPendientesPorZonaYRegistrador(Long idZona, Long idUsuarioRegistrador);
    ResponseEntity<List<DtoUsuarioOut>> getUsuariosPendientesResidente(Long idZona);
    ResponseEntity<ApiResponse> createUsuario(DtoUsuarioIn in);
    ResponseEntity<ApiResponse> deleteUsuario(Long idUsuario);
    ResponseEntity<ApiResponse> updateEstadoUsuario(Long idUsuario, DtoEstadoUsuariIn in, Authentication authentication);
    ResponseEntity<ApiResponse> updateContraseña(Long idUsuario, DtoUsuarioContraseñaIn in);
    boolean isRegistrador(String idUsuario);
}