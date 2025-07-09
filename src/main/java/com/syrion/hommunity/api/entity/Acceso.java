package com.syrion.hommunity.api.entity;

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
@Table(name = "acceso")
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceso")
    @JsonProperty("idAcceso")
    private Long idAcceso;

    @Column(name = "id_qr")
    @JsonProperty("idQr")
    private Long idQr;

    @Column(name = "tipo")
    @JsonProperty("tipo")
    private String tipoAcceso;

    @Column(name = "fecha")
    @JsonProperty("fecha")
    private LocalDateTime fecha;
}
