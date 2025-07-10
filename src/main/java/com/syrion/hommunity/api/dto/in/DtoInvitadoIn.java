package com.syrion.hommunity.api.dto.in;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "El nombre no puede estar en blanco") 
    private String nombre;

    @JsonProperty("apellidoPaterno")
    @NotNull(message = "El apellido paterno es obligatorio")
    @NotBlank(message = "El apellido paterno no puede estar en blanco")
    private String apellidoPaterno;

    @JsonProperty("apellidoMaterno")
    @NotBlank(message = "El apellido materno no puede estar en blanco")
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
