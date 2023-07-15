package com.example.publictransportationguidance.pojo.pathsResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class NearestPaths implements Serializable {

    @SerializedName("name")
    @Expose
    private String name="";

    @SerializedName("latitude")
    @Expose
    private double latitude=0.0;

    @SerializedName("longitude")
    @Expose
    private double longitude=0.0;

    @SerializedName("cost")
    @Expose
    private double cost=0.0;

//    @SerializedName("distance")
//    @Expose
//    private Double distance=0.0;

    @SerializedName("distance")
    @Expose
    private Object distance=0.0;

    @SerializedName("transportationType")
    @Expose
    private String transportationType="";

    @SerializedName("lineNumber")
    @Expose
    private String lineNumber="";

    @SerializedName("totalCost")
    @Expose
    private double totalCost=0.0;

    @SerializedName("totalDistance")
    @Expose
    private double totalDistance=0.0;

    @SerializedName("totalTime")
    @Expose
    private int totalTime=0;

    private final static long serialVersionUID = 2142764386547270924L;

    public NearestPaths() {}

    public NearestPaths(String name, double latitude, double longitude, double cost, double distance, String transportationType, String lineNumber, double totalCost, double totalDistance,int totalTime) {
        super(); this.name = name; this.latitude = latitude; this.longitude = longitude;
        this.cost = cost; this.distance = distance; this.transportationType = transportationType; this.lineNumber = lineNumber;
        this.totalCost = totalCost; this.totalDistance = totalDistance;
        this.totalTime=totalTime;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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

    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }

//    public Double getDistance() {
//        return distance;
//    }
//    public void setDistance(Double distance) {
//        this.distance = distance;
//    }

    public Object getDistance(){
        return distance;
    }

    public void setDistance(Object distance){
        this.distance=distance;
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

    public double getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getTotalDistance() {
        return totalDistance;
    }
    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

}
