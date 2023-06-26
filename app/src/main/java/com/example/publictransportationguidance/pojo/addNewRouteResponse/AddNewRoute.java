
package com.example.publictransportationguidance.pojo.addNewRouteResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddNewRoute {

    @SerializedName("records")
    @Expose
    private List<Object> records;
    @SerializedName("summary")
    @Expose
    private Summary summary;

    public List<Object> getRecords() {
        return records;
    }

    public void setRecords(List<Object> records) {
        this.records = records;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

}
