package com.syrion.hommunity.common.mapper;

import com.syrion.hommunity.api.enums.EstadoUsuario;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true) // se aplica a todos los campos EstadoUsuario automáticamente
public class MapperEstadoUsuario implements AttributeConverter<EstadoUsuario, String> {

    @Override
    public String convertToDatabaseColumn(EstadoUsuario estado) {
        return estado != null ? estado.getValor() : null;
    }

    @Override
    public EstadoUsuario convertToEntityAttribute(String valor) {
        return valor != null ? EstadoUsuario.fromValor(valor) : null;
    }
}
