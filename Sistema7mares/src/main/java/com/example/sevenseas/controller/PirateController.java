package com.example.sevenseas.controller;

import com.example.sevenseas.domain.entity.Pirate;
import com.example.sevenseas.service.PirateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pirates")
public class PirateController {

    private final PirateService pirateService;

    public PirateController(PirateService pirateService) {
        this.pirateService = pirateService;
    }

    @PostMapping
    public Pirate createPirate(@RequestBody Pirate pirate) {
        return pirateService.createPirate(pirate);
    }

    @GetMapping
    public List<Pirate> getAllPirates() {
        return pirateService.getAllPirates();
    }

    @GetMapping("/{id}")
    public Pirate getPirateById(@PathVariable UUID id) {
        return pirateService.getPirateById(id);
    }

    @PutMapping("/{id}")
    public Pirate updatePirate(@PathVariable UUID id, @RequestBody Pirate pirate) {
        return pirateService.updatePirate(id, pirate);
    }

    @DeleteMapping("/{id}")
    public String deletePirate(@PathVariable UUID id) {
        pirateService.deletePirate(id);
        return "Pirate deleted successfully";
    }
}

