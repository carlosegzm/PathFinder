package com.ai.PathFinder.dtos.genetic;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public class GeneticRequestDto {

    @NotNull(message = "bugdetlimit must not be null")
    private BigDecimal budgetLimit;

    public GeneticRequestDto(){}

    public GeneticRequestDto(BigDecimal budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    public BigDecimal getBudgetLimit() { return budgetLimit; }
    public void setBudgetLimit(BigDecimal budgetLimit) { this.budgetLimit = budgetLimit; }
}