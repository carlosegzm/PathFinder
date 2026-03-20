package com.ai.PathFinder.entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "capitals")
public class Capital {

    @Id
    private String id;
    
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;

}
