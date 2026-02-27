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

    private String origin;
    private String destination;
    private Integer distance;

}
