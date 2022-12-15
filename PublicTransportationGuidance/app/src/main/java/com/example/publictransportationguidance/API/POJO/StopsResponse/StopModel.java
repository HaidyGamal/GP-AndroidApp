package com.example.publictransportationguidance.API.POJO.StopsResponse;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName="Stops")
public class StopModel implements Serializable {
    @SerializedName("latitude")
    @Expose
    private Double latitude;

    @SerializedName("longtude")
    @Expose
    private Double longtude;

    @PrimaryKey
    @NotNull
    @SerializedName("name")
    @Expose
    private String name;

    private final static long serialVersionUID = 548236053408022721L;

    public StopModel() {}

    public StopModel(Double latitude, Double longtude, String name) {
        super();
        this.latitude = latitude;
        this.longtude = longtude;
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtude() {
        return longtude;
    }
    public void setLongtude(Double longtude) {
        this.longtude = longtude;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}

