package com.syrion.hommunity.api.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoFamiliaPersonasOut {

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

}
