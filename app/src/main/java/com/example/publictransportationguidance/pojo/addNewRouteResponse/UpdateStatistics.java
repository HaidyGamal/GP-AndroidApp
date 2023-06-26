
package com.example.publictransportationguidance.pojo.addNewRouteResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateStatistics {

    @SerializedName("_stats")
    @Expose
    private Stats__1 stats;
    @SerializedName("_systemUpdates")
    @Expose
    private Integer systemUpdates;
    @SerializedName("_containsUpdates")
    @Expose
    private Boolean containsUpdates;

    public Stats__1 getStats() {
        return stats;
    }

    public void setStats(Stats__1 stats) {
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
