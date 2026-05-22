package com.example.laboratorio3.services;

import com.example.laboratorio3.dto.request.CreateSpecimenRequest;
import com.example.laboratorio3.dto.request.UpdateSpecimenRequest;
import com.example.laboratorio3.dto.response.PageableResponse;
import com.example.laboratorio3.dto.response.SpecimenResponse;
import com.example.laboratorio3.entities.Specimen;
import com.example.laboratorio3.exceptions.ResourceNotFoundException;
import com.example.laboratorio3.mappers.SpecimenMapper;
import com.example.laboratorio3.repositories.SpecimenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpecimenServiceImpl implements SpecimenService {

    private final SpecimenRepository specimenRepository;
    private final SpecimenMapper specimenMapper;

    @Override
    @Transactional
    public SpecimenResponse createSpecimen(CreateSpecimenRequest request) {
        Specimen specimen = specimenMapper.toEntityCreate(request);
        return specimenMapper.toDto(specimenRepository.save(specimen));
    }

    @Override
    public PageableResponse<SpecimenResponse> getAllSpecimens(
            int page,
            int size,
            String sortBy,
            String sortOrder
    ) {
        Sort sort = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Specimen> specimenPage = specimenRepository.findAll(pageable);

        Page<SpecimenResponse> responsePage = specimenMapper.toDtoPage(specimenPage);

        return PageableResponse.<SpecimenResponse>builder()
                .content(responsePage.getContent())
                .pageNumber(responsePage.getNumber())
                .pageSize(responsePage.getSize())
                .totalElements(responsePage.getTotalElements())
                .totalPages(responsePage.getTotalPages())
                .last(responsePage.isLast())
                .build();
    }

    @Override
    public SpecimenResponse getSpecimenById(UUID id) {
        Specimen specimen = specimenRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Specimen not found in Sheikah Slate records")
                );

        return specimenMapper.toDto(specimen);
    }

    @Override
    @Transactional
    public SpecimenResponse updateSpecimen(UUID id, UpdateSpecimenRequest request) {
        getSpecimenById(id);

        Specimen specimen = specimenMapper.toEntityUpdate(request, id);

        return specimenMapper.toDto(specimenRepository.save(specimen));
    }

    @Override
    @Transactional
    public SpecimenResponse deleteSpecimen(UUID id) {
        SpecimenResponse existingSpecimen = getSpecimenById(id);
        specimenRepository.deleteById(id);
        return existingSpecimen;
    }
}