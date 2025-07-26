package com.syrion.hommunity.api.dto.in;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class DtoRenovarInvitadoIn {

    @NotNull(message = "La fecha de entrada no puede ser nula.")
    @Future(message = "La fecha de entrada debe ser en el futuro.")
    private LocalDateTime fechaEntrada;

    @NotNull(message = "La fecha de salida no puede ser nula.")
    private LocalDateTime fechaSalida;

    @NotNull(message = "El número de usos no puede ser nulo.")
    @Min(value = 1, message = "Debe haber al menos 1 uso disponible.")
    private Integer usosDisponibles;

    // Getters y Setters
    public LocalDateTime getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(LocalDateTime fechaEntrada) { this.fechaEntrada = fechaEntrada; }
    public LocalDateTime getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDateTime fechaSalida) { this.fechaSalida = fechaSalida; }
    public Integer getUsosDisponibles() { return usosDisponibles; }
    public void setUsosDisponibles(Integer usosDisponibles) { this.usosDisponibles = usosDisponibles; }
}