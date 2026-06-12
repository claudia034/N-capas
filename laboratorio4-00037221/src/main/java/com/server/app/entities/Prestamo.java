package com.server.app.entities;

import java.math.BigDecimal;

import com.server.app.entities.enums.PrestamoEstado;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "prestamos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "capital_solicitado", nullable = false, precision = 14, scale = 2)
    private BigDecimal capitalSolicitado;

    @Column(name = "tasa_interes_anual", nullable = false, precision = 7, scale = 4)
    private BigDecimal tasaInteresAnual;

    @Column(name = "plazo_meses", nullable = false)
    private Integer plazoMeses;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrestamoEstado estado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;
}
