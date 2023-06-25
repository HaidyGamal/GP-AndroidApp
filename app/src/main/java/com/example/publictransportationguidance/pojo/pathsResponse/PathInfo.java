package com.example.publictransportationguidance.pojo.pathsResponse;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@Entity(tableName = "Paths")
public class PathInfo {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NotNull
    private int defaultPathNumber;

    @NotNull
    private Double distance;

    @NotNull
    private int cost;

    @NotNull
    private int time;

    @NotNull
    private String path;

    @NotNull
    private String detailedPath;

    @NotNull
    @TypeConverters({CoordinatesConverter.class})
    private ArrayList<LatLng> coordinates;


    public PathInfo(int defaultPathNumber, Double distance, int cost, int time, String path,String detailedPath,ArrayList<LatLng> coordinates) {
        this.defaultPathNumber = defaultPathNumber;
        this.distance = distance;
        this.cost = cost;
        this.time = time;
        this.path = path;
        this.detailedPath=detailedPath;
        this.coordinates = coordinates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDefaultPathNumber() {
        return defaultPathNumber;
    }

    public void setDefaultPathNumber(int defaultPathNumber) {
        this.defaultPathNumber = defaultPathNumber;
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

    @NotNull
    public String getDetailedPath() {
        return detailedPath;
    }

    public void setDetailedPath(@NotNull String detailedPath) {
        this.detailedPath = detailedPath;
    }

    @NotNull
    @TypeConverters({CoordinatesConverter.class})
    public ArrayList<LatLng> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(@NotNull ArrayList<LatLng> coordinates) {
        this.coordinates = coordinates;
    }

}
