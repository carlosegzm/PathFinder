package com.ai.PathFinder.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "common_routes")
public class CommonRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String origin;
    private String destination;
    private Integer load;

}
