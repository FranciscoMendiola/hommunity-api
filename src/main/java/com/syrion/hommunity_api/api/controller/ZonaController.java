package com.syrion.hommunity_api.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.syrion.hommunity_api.api.dto.in.DtoZonaIn;
import com.syrion.hommunity_api.api.dto.out.DtoZonaOut;
import com.syrion.hommunity_api.api.service.SvcZona;
import com.syrion.hommunity_api.common.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/zona")
@Tag(name = "Zona", description = "Gestión de zonas")
public class ZonaController {

    private final SvcZona svcZona;

    public ZonaController(SvcZona svcZona) {
        this.svcZona = svcZona;
    }

    @PostMapping
    @Operation(summary = "Crear zona", description = "Permite crear una nueva zona en el sistema.")
    public ResponseEntity<ApiResponse> crearZona(@RequestBody DtoZonaIn dto) {
        return svcZona.crearZona(dto);
    }

    @GetMapping
    @Operation(summary = "Listar zonas", description = "Permite obtener una lista de todas las zonas registradas en el sistema.")
    public ResponseEntity<List<DtoZonaOut>> listarZonas() {
        return svcZona.obtenerZonas();
    }
}