package com.syrion.hommunity_api.api.entity;

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
@Table(name = "casa")
public class Casa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_casa")
    @JsonProperty("idCasa")
    private Long idCasa;

    @Column(name = "numero")
    @JsonProperty("numero")
    private String numero;

    @Column(name = "calle")
    @JsonProperty("calle")
    private String calle;

    @Column(name = "id_zona")
    @JsonProperty("idZona")
    private Long idZona;
}
