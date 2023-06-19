
package com.example.publictransportationguidance.pojo.estimatedTimeResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Step__1 {

    @SerializedName("distance")
    @Expose
    private Distance__2 distance;
    @SerializedName("duration")
    @Expose
    private Duration__2 duration;
    @SerializedName("end_location")
    @Expose
    private EndLocation__2 endLocation;
    @SerializedName("html_instructions")
    @Expose
    private String htmlInstructions;
    @SerializedName("polyline")
    @Expose
    private Polyline__1 polyline;
    @SerializedName("start_location")
    @Expose
    private StartLocation__2 startLocation;
    @SerializedName("travel_mode")
    @Expose
    private String travelMode;
    @SerializedName("maneuver")
    @Expose
    private String maneuver;

    public Distance__2 getDistance() {
        return distance;
    }

    public void setDistance(Distance__2 distance) {
        this.distance = distance;
    }

    public Duration__2 getDuration() {
        return duration;
    }

    public void setDuration(Duration__2 duration) {
        this.duration = duration;
    }

    public EndLocation__2 getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(EndLocation__2 endLocation) {
        this.endLocation = endLocation;
    }

    public String getHtmlInstructions() {
        return htmlInstructions;
    }

    public void setHtmlInstructions(String htmlInstructions) {
        this.htmlInstructions = htmlInstructions;
    }

    public Polyline__1 getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline__1 polyline) {
        this.polyline = polyline;
    }

    public StartLocation__2 getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(StartLocation__2 startLocation) {
        this.startLocation = startLocation;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }

}
