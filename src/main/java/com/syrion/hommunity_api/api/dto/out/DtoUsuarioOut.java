package com.syrion.hommunity_api.api.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.syrion.hommunity_api.api.enums.EstadoUsuario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoUsuarioOut {

    @JsonProperty("idusuario")
    private Long idUsuario;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellidoPaterno")
    private String apellidoPaterno;

    @JsonProperty("apellidoMaterno")
    private String apellidoMaterno;

    @JsonProperty("correo")
    private String correo;
    
    @JsonProperty("estado")
    private String estado;

    @JsonProperty("fotoIdentificacion")
    private String fotoIdentificacion;

    @JsonProperty("idRol")
    private Long idRol;

    @JsonProperty("idZona")
    private Long idZona;

    @JsonProperty("idFamilia")
    private Long idFamilia;
}
