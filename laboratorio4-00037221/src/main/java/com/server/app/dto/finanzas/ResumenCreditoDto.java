package com.server.app.dto.finanzas;

import java.math.BigDecimal;

public record ResumenCreditoDto(
        long totalPrestamos,
        long prestamosActivos,
        long prestamosPagados,
        long cuotasPendientes,
        BigDecimal capitalSolicitadoTotal,
        BigDecimal deudaPendiente
) {
}
