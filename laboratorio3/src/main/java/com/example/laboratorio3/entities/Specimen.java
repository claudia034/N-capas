package com.example.laboratorio3.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@Table(name = "Specimen")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Specimen {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "region")
    private String region;

    @Column(name = "dangerLevel")
    private Integer dangerLevel;

    @Column(name = "isFriendly")
    private Boolean isFriendly;
}