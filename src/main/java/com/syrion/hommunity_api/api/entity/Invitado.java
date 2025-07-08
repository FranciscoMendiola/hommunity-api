package com.syrion.hommunity_api.api.entity;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "invitado")
public class Invitado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_invitado")
    @JsonProperty("idInvitado")
    private Long idInvitado;

    @Column(name = "nombre")
    @JsonProperty("nombre")
    private String nombre;

    @Column(name = "apellido_paterno")
    @JsonProperty("apellidoPaterno")
    private String apellidoPaterno;

    @Column(name = "apellido_materno")
    @JsonProperty("apellidoMaterno")
    private String apellidoMaterno;

    @Column(name = "fecha_entrada")
    @JsonProperty("fechaEntrada")
    private LocalDateTime fechaEntrada;
    
    @Column(name = "fecha_salida")
    @JsonProperty("fechaSalida")
    private LocalDateTime fechaSalida;

    @Column(name = "id_usuario")
    @JsonProperty("idUsuario")
    private Long idUsuario;
}
