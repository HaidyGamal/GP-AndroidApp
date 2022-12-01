package com.example.publictransportationguidance.API.POJO.ShortestPathResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Route implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("lineNumber")
    @Expose
    private String lineNumber;

    private final static long serialVersionUID = 7012308176479429482L;

    public Route() {}

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLineNumber() {
        return lineNumber;
    }
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }
}
