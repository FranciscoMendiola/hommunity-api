package com.syrion.hommunity.api.enums;

public enum EstadoUsuario {
    PENDIENTE("pendiente"),
    APROBADO("aprobado");

    private final String valor;

    EstadoUsuario(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoUsuario fromValor(String valor) {
        for (EstadoUsuario estado : values()) {
            if (estado.valor.equalsIgnoreCase(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("EstadoUsuario no válido: " + valor);
    }
}
