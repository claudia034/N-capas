package com.server.app.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.server.app.entities.enums.PlanPagoEstado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "planes_pago", uniqueConstraints = @UniqueConstraint(columnNames = { "prestamo_id", "numero_cuota" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cuota", nullable = false)
    private Integer numeroCuota;

    @Column(name = "monto_capital", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoCapital;

    @Column(name = "monto_interes", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoInteres;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlanPagoEstado estado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prestamo_id", nullable = false)
    private Prestamo prestamo;
}
