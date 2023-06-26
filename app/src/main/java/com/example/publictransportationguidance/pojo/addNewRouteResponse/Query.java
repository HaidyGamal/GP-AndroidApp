
package com.example.publictransportationguidance.pojo.addNewRouteResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Query {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("parameters")
    @Expose
    private Parameters parameters;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

}
