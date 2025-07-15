package com.syrion.hommunity.api.dto.in;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DtoUsuarioIn{

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

    @JsonProperty("correo")
    @NotNull(message = "El correo es obligatorio")
    @NotBlank(message = "El correo no puede estar en blanco")
    @Pattern(
        regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "El correo no tiene un formato válido"
    )
    private String correo;

    @JsonProperty("contrasena")
    @NotNull(message = "La contraseña es obligatoria")
    @NotBlank(message = "La contraseña no puede estar en blanco")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?~\\\\\\-=\\/|]).{8,}$",
        message = "La contrasena debe tener al menos 8 caracteres, incluir una mayúscula y un carácter especial"
    )
    private String contrasena;

    @JsonProperty("fotoIdentificacion")
    @NotNull(message = "La foto de identificación es obligatoria")
    private MultipartFile fotoIdentificacion;

    @JsonProperty("idZona")
    @NotNull(message = "La zona es obligatoria")
    private Long idZona;
    
    @JsonProperty("idFamilia")
    private Long idFamilia;
}