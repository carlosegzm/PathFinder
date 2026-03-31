package com.ai.PathFinder.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "common_routes")
public class CommonRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origin_id", nullable = false)
    private Capital origin;

    @ManyToOne
    @JoinColumn(name = "destination_id", nullable = false)
    private Capital destination;

    @Column(name = "load", nullable = false)
    private Integer load;
}