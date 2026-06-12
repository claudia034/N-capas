package com.server.app.repositories.finanzas;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.server.app.entities.PlanPago;
import com.server.app.entities.enums.PlanPagoEstado;

public interface PlanPagoRepository extends JpaRepository<PlanPago, Long> {

    Page<PlanPago> findByPrestamoIdAndPrestamoUsuarioId(Long prestamoId, int usuarioId, Pageable pageable);

    Optional<PlanPago> findByIdAndPrestamoUsuarioId(Long id, int usuarioId);

    List<PlanPago> findByPrestamoIdOrderByNumeroCuotaAsc(Long prestamoId);

    boolean existsByPrestamoIdAndEstado(Long prestamoId, PlanPagoEstado estado);
}
