package com.syrion.hommunity.api.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DtoQrUsuarioCodigoOut {

    @JsonProperty("codigo")
    private String codigo;

    public DtoQrUsuarioCodigoOut(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
