package com.syrion.hommunity_api.common.mapper;

import com.syrion.hommunity_api.api.enums.TipoAcceso;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MapperTipoAcceso implements AttributeConverter<TipoAcceso, String> {

    @Override
    public String convertToDatabaseColumn(TipoAcceso tipo) {
        return tipo != null ? tipo.getValor() : null;
    }

    @Override
    public TipoAcceso convertToEntityAttribute(String valor) {
        return valor != null ? TipoAcceso.fromValor(valor) : null;
    }
}