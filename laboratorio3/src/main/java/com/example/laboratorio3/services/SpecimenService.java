package com.example.laboratorio3.services;
import com.example.laboratorio3.dto.request.CreateSpecimenRequest;
import com.example.laboratorio3.dto.request.UpdateSpecimenRequest;
import com.example.laboratorio3.dto.response.PageableResponse;
import com.example.laboratorio3.dto.response.SpecimenResponse;

import java.util.UUID;

public interface SpecimenService {

    SpecimenResponse createSpecimen(CreateSpecimenRequest request);

    PageableResponse<SpecimenResponse> getAllSpecimens(
            int page,
            int size,
            String sortBy,
            String sortOrder
    );

    SpecimenResponse getSpecimenById(UUID id);

    SpecimenResponse updateSpecimen(UUID id, UpdateSpecimenRequest request);

    SpecimenResponse deleteSpecimen(UUID id);
}
