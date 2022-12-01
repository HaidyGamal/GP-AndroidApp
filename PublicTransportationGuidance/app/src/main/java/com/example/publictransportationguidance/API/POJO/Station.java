package com.example.publictransportationguidance.API.POJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Station implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;
    private final static long serialVersionUID = 2515921688977238402L;

    public String getStationName() {
        return name;
    }

    public void setStationName(String name) {
        this.name = name;
    }

}

