package com.ai.PathFinder.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "path_between_capitals")
public class PathBetweenCapitals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origin_id")
    private Capital origin;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Capital destination;

    private Integer distance;
    private boolean hasRailway;

    public boolean hasRailway(){ return hasRailway; }
}
