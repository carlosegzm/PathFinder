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

    @ManyToOne
    @JoinColumn(name = "origin_id")
    private Capital origin;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Capital destination;
    
    private Integer load;

}
