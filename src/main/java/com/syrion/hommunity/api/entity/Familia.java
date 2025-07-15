package com.syrion.hommunity.api.entity;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

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
@Table(name = "familia")
public class Familia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_familia")
    @JsonProperty("idFamilia")
    private Long idFamilia;

    @Column(name = "apellido")
    @JsonProperty("apellido")
    private String apellido;

    @Column(name = "estado")
    @JsonProperty("estado")
    private String estado;

    @Column(name = "foto_identificacion")
    @JsonProperty("fotoIdentificacion")
    private String fotoIdentificacion;

    @Column(name = "fecha_registro")
    @JsonProperty("fechaRegistro")
    private LocalDateTime fechaRegistro;

    @Column(name = "id_casa")
    @JsonProperty("idCasa")
    private Long idCasa;
    
    @Column(name = "id_usuario_registrador")
    @JsonProperty("idUsuarioRegistrador")
    private Long idUsuarioRegistrador;
}
