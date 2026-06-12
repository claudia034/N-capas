package com.server.app.services.finanzas;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.app.dto.finanzas.AbonoCreateDto;
import com.server.app.dto.finanzas.PrestamoCreateDto;
import com.server.app.dto.finanzas.ResumenCreditoDto;
import com.server.app.entities.Abono;
import com.server.app.entities.PlanPago;
import com.server.app.entities.Prestamo;
import com.server.app.entities.User;
import com.server.app.entities.enums.PlanPagoEstado;
import com.server.app.entities.enums.PrestamoEstado;
import com.server.app.exceptions.BadRequestException;
import com.server.app.exceptions.ConfictException;
import com.server.app.exceptions.NotFoundException;
import com.server.app.repositories.finanzas.AbonoRepository;
import com.server.app.repositories.finanzas.PlanPagoRepository;
import com.server.app.repositories.finanzas.PrestamoRepository;

@Service
public class PrestamoService {

    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal TWELVE = new BigDecimal("12");
    private static final BigDecimal DAILY_LATE_FEE_RATE = new BigDecimal("0.005");

    private final PrestamoRepository prestamoRepository;
    private final PlanPagoRepository planPagoRepository;
    private final AbonoRepository abonoRepository;

    public PrestamoService(PrestamoRepository prestamoRepository,
                           PlanPagoRepository planPagoRepository,
                           AbonoRepository abonoRepository) {
        this.prestamoRepository = prestamoRepository;
        this.planPagoRepository = planPagoRepository;
        this.abonoRepository = abonoRepository;
    }

    @Transactional(readOnly = true)
    public Page<Prestamo> findPrestamos(User user, int page, int size) {
        return prestamoRepository.findByUsuarioId(user.getId(), PageRequest.of(page, size));
    }

    @Transactional
    public Prestamo crearPrestamo(User user, PrestamoCreateDto dto) {
        Prestamo prestamo = new Prestamo();
        prestamo.setCapitalSolicitado(toMoney(dto.getCapitalSolicitado()));
        prestamo.setTasaInteresAnual(dto.getTasaInteresAnual());
        prestamo.setPlazoMeses(dto.getPlazoMeses());
        prestamo.setEstado(PrestamoEstado.APROBADO);
        prestamo.setUsuario(user);

        Prestamo saved = prestamoRepository.save(prestamo);
        planPagoRepository.saveAll(buildPlanPagos(saved));
        return saved;
    }

    @Transactional(readOnly = true)
    public Page<PlanPago> findPlanesPago(User user, Long prestamoId, int page, int size) {
        prestamoRepository.findByIdAndUsuarioId(prestamoId, user.getId())
                .orElseThrow(() -> new NotFoundException("Préstamo no encontrado"));
        return planPagoRepository.findByPrestamoIdAndPrestamoUsuarioId(prestamoId, user.getId(), PageRequest.of(page, size));
    }

    @Transactional
    public Abono registrarAbono(User user, AbonoCreateDto dto) {
        PlanPago planPago = planPagoRepository.findByIdAndPrestamoUsuarioId(dto.getPlanPagoId(), user.getId())
                .orElseThrow(() -> new NotFoundException("Plan de pago no encontrado"));

        if (planPago.getEstado() == PlanPagoEstado.PAGADO) {
            throw new ConfictException("La cuota ya fue pagada");
        }

        LocalDateTime fechaPago = dto.getFechaPago() == null ? LocalDateTime.now() : dto.getFechaPago();
        BigDecimal recargoMora = calculateLateFee(planPago, fechaPago);
        BigDecimal montoRequerido = planPago.getMontoCapital()
                .add(planPago.getMontoInteres())
                .add(recargoMora);

        if (dto.getMonto().compareTo(montoRequerido) < 0) {
            throw new BadRequestException("El monto debe cubrir capital, interés y mora. Monto requerido: " + toMoney(montoRequerido));
        }

        planPago.setEstado(PlanPagoEstado.PAGADO);
        planPagoRepository.save(planPago);

        Prestamo prestamo = planPago.getPrestamo();
        if (!planPagoRepository.existsByPrestamoIdAndEstado(prestamo.getId(), PlanPagoEstado.PENDIENTE)) {
            prestamo.setEstado(PrestamoEstado.PAGADO);
            prestamoRepository.save(prestamo);
        }

        Abono abono = new Abono();
        abono.setMonto(toMoney(dto.getMonto()));
        abono.setFechaPago(fechaPago);
        abono.setRecargoMora(recargoMora);
        abono.setPlanPago(planPago);
        return abonoRepository.save(abono);
    }

    @Transactional(readOnly = true)
    public ResumenCreditoDto resumenCredito(User user) {
        List<Prestamo> prestamos = prestamoRepository.findByUsuarioId(user.getId());

        long prestamosPagados = prestamos.stream()
                .filter(prestamo -> prestamo.getEstado() == PrestamoEstado.PAGADO)
                .count();

        BigDecimal capitalTotal = prestamos.stream()
                .map(Prestamo::getCapitalSolicitado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long cuotasPendientes = 0;
        BigDecimal deudaPendiente = BigDecimal.ZERO;
        for (Prestamo prestamo : prestamos) {
            List<PlanPago> planes = planPagoRepository.findByPrestamoIdOrderByNumeroCuotaAsc(prestamo.getId());
            for (PlanPago plan : planes) {
                if (plan.getEstado() == PlanPagoEstado.PENDIENTE) {
                    cuotasPendientes++;
                    deudaPendiente = deudaPendiente.add(plan.getMontoCapital()).add(plan.getMontoInteres());
                }
            }
        }

        return new ResumenCreditoDto(
                prestamos.size(),
                prestamos.size() - prestamosPagados,
                prestamosPagados,
                cuotasPendientes,
                toMoney(capitalTotal),
                toMoney(deudaPendiente)
        );
    }

    private List<PlanPago> buildPlanPagos(Prestamo prestamo) {
        int months = prestamo.getPlazoMeses();
        BigDecimal balance = prestamo.getCapitalSolicitado();
        BigDecimal monthlyRate = prestamo.getTasaInteresAnual()
                .divide(ONE_HUNDRED, 12, RoundingMode.HALF_UP)
                .divide(TWELVE, 12, RoundingMode.HALF_UP);
        BigDecimal monthlyPayment = calculateMonthlyPayment(balance, monthlyRate, months);
        LocalDate start = LocalDate.now();

        java.util.ArrayList<PlanPago> planes = new java.util.ArrayList<>();
        for (int i = 1; i <= months; i++) {
            BigDecimal interest = toMoney(balance.multiply(monthlyRate, MATH_CONTEXT));
            BigDecimal principal = monthlyPayment.subtract(interest);

            if (i == months) {
                principal = balance;
                monthlyPayment = principal.add(interest);
            }

            principal = toMoney(principal);
            balance = balance.subtract(principal);

            PlanPago plan = new PlanPago();
            plan.setNumeroCuota(i);
            plan.setMontoCapital(principal);
            plan.setMontoInteres(interest);
            plan.setFechaVencimiento(start.plusMonths(i));
            plan.setEstado(PlanPagoEstado.PENDIENTE);
            plan.setPrestamo(prestamo);
            planes.add(plan);
        }

        return planes;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal monthlyRate, int months) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return toMoney(principal.divide(new BigDecimal(months), 2, RoundingMode.HALF_UP));
        }

        BigDecimal factor = BigDecimal.ONE.add(monthlyRate, MATH_CONTEXT).pow(months, MATH_CONTEXT);
        BigDecimal numerator = principal.multiply(monthlyRate, MATH_CONTEXT).multiply(factor, MATH_CONTEXT);
        BigDecimal denominator = factor.subtract(BigDecimal.ONE, MATH_CONTEXT);
        return toMoney(numerator.divide(denominator, 10, RoundingMode.HALF_UP));
    }

    private BigDecimal calculateLateFee(PlanPago planPago, LocalDateTime fechaPago) {
        long daysLate = ChronoUnit.DAYS.between(planPago.getFechaVencimiento(), fechaPago.toLocalDate());
        if (daysLate <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal base = planPago.getMontoCapital().add(planPago.getMontoInteres());
        return toMoney(base.multiply(DAILY_LATE_FEE_RATE).multiply(new BigDecimal(daysLate)));
    }

    private BigDecimal toMoney(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
