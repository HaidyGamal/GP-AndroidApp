package com.example.publictransportationguidance.pojo.pathsResponse;

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
    private int time;

    @NotNull
    private String path;

    public PathInfo(int id,Double distance, int cost, int time,String path) {
        this.id=id;
        this.distance = distance;
        this.cost = cost;
        this.time = time;
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

    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
}

