package com.server.app.dto.finanzas;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AbonoCreateDto {

    @NotNull(message = "El plan de pago es obligatorio")
    @Positive(message = "El plan de pago debe ser un ID positivo")
    @JsonAlias("plan_pago_id")
    private Long planPagoId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    @JsonAlias("fecha_pago")
    private LocalDateTime fechaPago;
}
