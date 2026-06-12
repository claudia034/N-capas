package com.server.app.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.app.dto.finanzas.AbonoCreateDto;
import com.server.app.dto.finanzas.PrestamoCreateDto;
import com.server.app.dto.finanzas.ResumenCreditoDto;
import com.server.app.dto.response.Pagination;
import com.server.app.dto.response.PaginationMeta;
import com.server.app.entities.Abono;
import com.server.app.entities.PlanPago;
import com.server.app.entities.Prestamo;
import com.server.app.entities.User;
import com.server.app.exceptions.UnauthorizedException;
import com.server.app.services.finanzas.PrestamoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/finanzas")
public class FinanzasController {

    private final PrestamoService prestamoService;

    public FinanzasController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping("/prestamos")
    public ResponseEntity<Pagination<Prestamo>> prestamos(Authentication authentication,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Page<Prestamo> prestamos = prestamoService.findPrestamos(currentUser(authentication), page, size);
        return ResponseEntity.ok(toPagination(prestamos));
    }

    @PostMapping("/prestamos")
    public ResponseEntity<Prestamo> crearPrestamo(Authentication authentication,
                                                  @Valid @RequestBody PrestamoCreateDto dto) {
        return ResponseEntity.ok(prestamoService.crearPrestamo(currentUser(authentication), dto));
    }

    @GetMapping("/prestamos/{id}/planes-pago")
    public ResponseEntity<Pagination<PlanPago>> planesPago(Authentication authentication,
                                                           @PathVariable Long id,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Page<PlanPago> planes = prestamoService.findPlanesPago(currentUser(authentication), id, page, size);
        return ResponseEntity.ok(toPagination(planes));
    }

    @PostMapping("/abonos")
    public ResponseEntity<Abono> registrarAbono(Authentication authentication,
                                                @Valid @RequestBody AbonoCreateDto dto) {
        return ResponseEntity.ok(prestamoService.registrarAbono(currentUser(authentication), dto));
    }

    @GetMapping("/resumen-credito")
    public ResponseEntity<ResumenCreditoDto> resumenCredito(Authentication authentication) {
        return ResponseEntity.ok(prestamoService.resumenCredito(currentUser(authentication)));
    }

    private <T> Pagination<T> toPagination(Page<T> page) {
        return new Pagination<>(
                page.getContent(),
                new PaginationMeta(
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalPages(),
                        page.getTotalElements()
                )
        );
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new UnauthorizedException("Usuario no autenticado");
        }
        return user;
    }
}
