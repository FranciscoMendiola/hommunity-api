package com.syrion.hommunity_api.api.dto.in;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoInvitadoIn {

    @JsonProperty("nombre")
    @NotNull(message = "El nombre es obligatorio") 
    private String nombre;

    @JsonProperty("apellidoPaterno")
    @NotNull(message = "El apellido paterno es obligatorio")
    private String apellidoPaterno;

    @JsonProperty("apellidoMaterno")
    private String apellidoMaterno;

    @JsonProperty("fechaEntrada")
    @NotNull(message = "La fecha de entrada es obligatoria")
    private LocalDateTime fechaEntrada;
    
    @JsonProperty("fechaSalida")
    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDateTime fechaSalida;

    @JsonProperty("idUsuario")
    @NotNull(message = "El usuario residente es obligatorio")
    private Long idUsuario;
}
