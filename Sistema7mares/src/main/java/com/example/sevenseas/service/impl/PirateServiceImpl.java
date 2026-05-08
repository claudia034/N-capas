package com.example.sevenseas.service.impl;

import com.example.sevenseas.domain.entity.Pirate;
import com.example.sevenseas.repository.PirateRepository;
import com.example.sevenseas.service.PirateService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PirateServiceImpl implements PirateService {

    private final PirateRepository pirateRepository;

    public PirateServiceImpl(PirateRepository pirateRepository) {
        this.pirateRepository = pirateRepository;
    }

    @Override
    public Pirate createPirate(Pirate pirate) {
        return pirateRepository.save(pirate);
    }

    @Override
    public List<Pirate> getAllPirates() {
        return pirateRepository.findAll();
    }

    @Override
    public Pirate getPirateById(UUID id) {
        return pirateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pirate not found"));
    }

    @Override
    public Pirate updatePirate(UUID id, Pirate pirateDetails) {
        Pirate pirate = pirateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pirate not found"));

        pirate.setName(pirateDetails.getName());
        pirate.setBounty(pirateDetails.getBounty());
        pirate.setCrew(pirateDetails.getCrew());
        pirate.setIsAlive(pirateDetails.getIsAlive());

        return pirateRepository.save(pirate);
    }

    @Override
    public void deletePirate(UUID id) {
        Pirate pirate = pirateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pirate not found"));

        pirateRepository.delete(pirate);
    }
}