package com.syrion.hommunity.api.entity;

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
@Table(name = "zona")
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_zona")
    @JsonProperty("idZona")
    private Long idZona;
    
    @Column(name = "nombre")
    @JsonProperty("nombre")
    private String nombre;

    @Column(name = "codigo_postal")
    @JsonProperty("codigoPostal")
    private Integer codigoPostal;

    @Column(name = "municipio")
    @JsonProperty("municipio")
    private String municipio;

    @Column(name = "colonia")
    @JsonProperty("colonia")
    private String colonia;
}
