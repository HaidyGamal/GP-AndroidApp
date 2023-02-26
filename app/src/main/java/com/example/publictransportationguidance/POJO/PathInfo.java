package com.example.publictransportationguidance.POJO;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "Paths")
public class PathInfo {

    @PrimaryKey
    private int id;

    @NotNull
    private Double distance;

    @NotNull
    private int cost;

    @NotNull
    private String path;

    public PathInfo(int id,Double distance, int cost, String path) {
        this.id=id;
        this.distance = distance;
        this.cost = cost;
        this.path = path;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Double getDistance() {
        return distance;
    }
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public int getCost() {
        return cost;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
}

