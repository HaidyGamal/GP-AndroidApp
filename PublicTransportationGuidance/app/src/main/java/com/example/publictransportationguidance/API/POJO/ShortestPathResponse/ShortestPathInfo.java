package com.example.publictransportationguidance.API.POJO.ShortestPathResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class ShortestPathInfo implements Serializable {

    @SerializedName("endNode")
    @Expose
    private String endNode;

    @SerializedName("routes")
    @Expose
    private List<List<Route>> routes = null;

    @SerializedName("recordsLength")
    @Expose
    private Integer recordsLength;

    @SerializedName("length")
    @Expose
    private Integer length;

    private final static long serialVersionUID = -8833493604685079045L;

    public ShortestPathInfo() {}

    public String getEndNode() {
        return endNode;
    }
    public void setEndNode(String endNode) {
        this.endNode = endNode;
    }

    public List<List<Route>> getRoutes() {
        return routes;
    }
    public void setRoutes(List<List<Route>> routes) {
        this.routes = routes;
    }

    public Integer getRecordsLength() {
        return recordsLength;
    }
    public void setRecordsLength(Integer recordsLength) {
        this.recordsLength = recordsLength;
    }

    public Integer getLength() {
        return length;
    }
    public void setLength(Integer length) {
        this.length = length;
    }


}
