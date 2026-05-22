package com.example.laboratorio3.controllers;

import com.example.laboratorio3.dto.request.CreateSpecimenRequest;
import com.example.laboratorio3.dto.request.UpdateSpecimenRequest;
import com.example.laboratorio3.dto.response.GeneralResponse;
import com.example.laboratorio3.dto.response.PageableResponse;
import com.example.laboratorio3.dto.response.SpecimenResponse;
import com.example.laboratorio3.services.SpecimenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/specimens")
@RequiredArgsConstructor
public class SpecimenController {

    private final SpecimenService specimenService;

    private <T> ResponseEntity<GeneralResponse<T>> buildResponse(
            String message,
            HttpStatus status,
            T data,
            HttpServletRequest request
    ) {
        GeneralResponse<T> response = GeneralResponse.<T>builder()
                .message(message)
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .data(data)
                .build();

        return ResponseEntity.status(status).body(response);
    }

    @PostMapping
    public ResponseEntity<GeneralResponse<SpecimenResponse>> createSpecimen(
            @Valid @RequestBody CreateSpecimenRequest requestBody,
            HttpServletRequest request
    ) {
        return buildResponse(
                "Specimen registered successfully",
                HttpStatus.CREATED,
                specimenService.createSpecimen(requestBody),
                request
        );
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<PageableResponse<SpecimenResponse>>> getAllSpecimens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            HttpServletRequest request
    ) {
        return buildResponse(
                "Specimens retrieved successfully",
                HttpStatus.OK,
                specimenService.getAllSpecimens(page, size, sortBy, sortOrder),
                request
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<SpecimenResponse>> getSpecimenById(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        return buildResponse(
                "Specimen retrieved successfully",
                HttpStatus.OK,
                specimenService.getSpecimenById(id),
                request
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse<SpecimenResponse>> updateSpecimen(
            @PathVariable UUID id,
            @RequestBody UpdateSpecimenRequest requestBody,
            HttpServletRequest request
    ) {
        return buildResponse(
                "Specimen updated successfully",
                HttpStatus.OK,
                specimenService.updateSpecimen(id, requestBody),
                request
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<SpecimenResponse>> deleteSpecimen(
            @PathVariable UUID id,
            HttpServletRequest request
    ) {
        return buildResponse(
                "Specimen deleted successfully",
                HttpStatus.OK,
                specimenService.deleteSpecimen(id),
                request
        );
    }
}