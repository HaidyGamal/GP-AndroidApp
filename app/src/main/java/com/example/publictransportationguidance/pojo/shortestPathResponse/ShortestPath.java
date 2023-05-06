package com.example.publictransportationguidance.pojo.shortestPathResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ShortestPath implements Serializable {

    @SerializedName("name")
    @Expose
    private String name="";

    @SerializedName("latitude")
    @Expose
    private Double latitude=0.0;

    @SerializedName("longitude")
    @Expose
    private Double longitude=0.0;

    @SerializedName("cost")
    @Expose
    private Integer cost=0;

    @SerializedName("distance")
    @Expose
    private Double distance=0.0;

    @SerializedName("transportationType")
    @Expose
    private String transportationType="";

    @SerializedName("lineNumber")
    @Expose
    private String lineNumber="";

    @SerializedName("totalCost")
    @Expose
    private Integer totalCost=0;

    @SerializedName("totalDistance")
    @Expose
    private Double totalDistance=0.0;

    private final static long serialVersionUID = 2142764386547270924L;

    public ShortestPath() {}

    public ShortestPath(String name, Double latitude, Double longitude, Integer cost, Double distance, String transportationType, String lineNumber, Integer totalCost, Double totalDistance) {
        super(); this.name = name; this.latitude = latitude; this.longitude = longitude;
        this.cost = cost; this.distance = distance; this.transportationType = transportationType; this.lineNumber = lineNumber;
        this.totalCost = totalCost; this.totalDistance = totalDistance;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getCost() {
        return cost;
    }
    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Double getDistance() {
        return distance;
    }
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getTransportationType() {
        return transportationType;
    }
    public void setTransportationType(String transportationType) { this.transportationType = transportationType; }

    public String getLineNumber() {
        return lineNumber;
    }
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Integer getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }
    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }



}
