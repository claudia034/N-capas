package com.example.sevenseas.domain.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "pirates")
public class Pirate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private Double bounty;
    private String crew;
    private Boolean isAlive;

    public Pirate() {
    }

    public Pirate(UUID id, String name, Double bounty, String crew, Boolean isAlive) {
        this.id = id;
        this.name = name;
        this.bounty = bounty;
        this.crew = crew;
        this.isAlive = isAlive;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Double getBounty() {
        return bounty;
    }

    public String getCrew() {
        return crew;
    }

    public Boolean getIsAlive() {
        return isAlive;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBounty(Double bounty) {
        this.bounty = bounty;
    }

    public void setCrew(String crew) {
        this.crew = crew;
    }

    public void setIsAlive(Boolean alive) {
        isAlive = alive;
    }
}