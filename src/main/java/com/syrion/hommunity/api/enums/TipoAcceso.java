package com.syrion.hommunity.api.enums;

public enum TipoAcceso {
    ENTRADA("entrada"),
    SALIDA("salida");

    private final String valor;

    TipoAcceso(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static TipoAcceso fromValor(String valor) {
        for (TipoAcceso estado : values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("TipoAcceso no válido: " + valor);
    }
}
