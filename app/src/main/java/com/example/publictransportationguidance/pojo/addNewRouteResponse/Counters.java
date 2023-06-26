
package com.example.publictransportationguidance.pojo.addNewRouteResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Counters {

    @SerializedName("_stats")
    @Expose
    private Stats stats;
    @SerializedName("_systemUpdates")
    @Expose
    private Integer systemUpdates;
    @SerializedName("_containsUpdates")
    @Expose
    private Boolean containsUpdates;

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public Integer getSystemUpdates() {
        return systemUpdates;
    }

    public void setSystemUpdates(Integer systemUpdates) {
        this.systemUpdates = systemUpdates;
    }

    public Boolean getContainsUpdates() {
        return containsUpdates;
    }

    public void setContainsUpdates(Boolean containsUpdates) {
        this.containsUpdates = containsUpdates;
    }

}
