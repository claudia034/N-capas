package com.server.app.dto.finanzas;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PrestamoCreateDto {

    @NotNull(message = "El capital solicitado es obligatorio")
    @DecimalMin(value = "0.01", message = "El capital solicitado debe ser mayor a cero")
    @JsonAlias("capital_solicitado")
    private BigDecimal capitalSolicitado;

    @NotNull(message = "La tasa de interés anual es obligatoria")
    @DecimalMin(value = "0.00", message = "La tasa de interés anual no puede ser negativa")
    @JsonAlias("tasa_interes_anual")
    private BigDecimal tasaInteresAnual;

    @NotNull(message = "El plazo en meses es obligatorio")
    @Min(value = 1, message = "El plazo debe ser de al menos 1 mes")
    @JsonAlias("plazo_meses")
    private Integer plazoMeses;
}
