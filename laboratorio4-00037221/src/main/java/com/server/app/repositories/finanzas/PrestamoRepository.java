package com.server.app.repositories.finanzas;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.server.app.entities.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    Page<Prestamo> findByUsuarioId(int usuarioId, Pageable pageable);

    List<Prestamo> findByUsuarioId(int usuarioId);

    Optional<Prestamo> findByIdAndUsuarioId(Long id, int usuarioId);
}
