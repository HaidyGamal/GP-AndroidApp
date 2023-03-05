package com.example.publictransportationguidance.POJO.StopsResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AllStops implements Serializable {

    @SerializedName("allNodes")
    @Expose
    private List<StopModel> allNodes = null;
    private final static long serialVersionUID = 3087340651516749579L;

    public AllStops() {}

    public AllStops(List<StopModel> allNodes) {
        super();
        this.allNodes = allNodes;
    }

    public List<StopModel> getAllNodes() {
        return allNodes;
    }
    public void setAllNodes(List<StopModel> allNodes) {
        this.allNodes = allNodes;
    }


}
