package com.ai.PathFinder.strategy.graph;

/**
 * Representa um vértice (nó) no grafo, correspondendo a uma localização geográfica (capital).
 * Armazena a identificação única da cidade e suas coordenadas geográficas (latitude e longitude) 
 * para cálculos de distância e mapeamento.
 */
public class Node {
    
    private String id;
    private double latitude;
    private double longitude;
    
    public Node(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
