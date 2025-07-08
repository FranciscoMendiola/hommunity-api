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
@Table(name = "qr")
public class QR {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_qr")
    @JsonProperty("idQr")
    private Long idQr;

    @Column(name = "codigo")
    @JsonProperty("codigo")
    private String codigo;

    @Column(name = "fecha_reacion")
    @JsonProperty("fechaCreacion")
    private LocalDateTime fechaCreacion;
    
    @Column(name = "vigente")
    @JsonProperty("vigente")
    private Boolean vigente;
    
    @Column(name = "usos_disponibles")
    @JsonProperty("usosDisponibles")
    private Integer usosDisponibles;

    @Column(name = "id_invitado")
    @JsonProperty("idInvitado")
    private Long idInvitado;
    
    @Column(name = "id_usuario")
    @JsonProperty("idUsuario")
    private Long idUsuario;
}
