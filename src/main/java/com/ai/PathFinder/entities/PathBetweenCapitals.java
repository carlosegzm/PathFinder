package com.ai.PathFinder.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "path_between_capitals")
public class PathBetweenCapitals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origin_id", nullable = false)
    private Capital origin;

    @ManyToOne
    @JoinColumn(name = "destination_id", nullable = false)
    private Capital destination;

    @Column(name = "distance", nullable = false)
    private Integer distance;

    @Column(name = "has_railway")
    private Boolean hasRailway = false;
}