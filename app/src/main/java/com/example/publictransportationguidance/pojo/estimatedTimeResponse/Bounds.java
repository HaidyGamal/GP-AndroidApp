package com.example.publictransportationguidance.pojo.estimatedTimeResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bounds {

    @SerializedName("northeast")
    @Expose
    private NorthEast northeast;
    @SerializedName("southwest")
    @Expose
    private SouthWest southwest;

    public NorthEast getNortheast() {
        return northeast;
    }

    public void setNortheast(NorthEast northeast) {
        this.northeast = northeast;
    }

    public SouthWest getSouthwest() {
        return southwest;
    }

    public void setSouthwest(SouthWest southwest) {
        this.southwest = southwest;
    }

}
