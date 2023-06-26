
package com.example.publictransportationguidance.pojo.addNewRouteResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stats {

    @SerializedName("nodesCreated")
    @Expose
    private Integer nodesCreated;
    @SerializedName("nodesDeleted")
    @Expose
    private Integer nodesDeleted;
    @SerializedName("relationshipsCreated")
    @Expose
    private Integer relationshipsCreated;
    @SerializedName("relationshipsDeleted")
    @Expose
    private Integer relationshipsDeleted;
    @SerializedName("propertiesSet")
    @Expose
    private Integer propertiesSet;
    @SerializedName("labelsAdded")
    @Expose
    private Integer labelsAdded;
    @SerializedName("labelsRemoved")
    @Expose
    private Integer labelsRemoved;
    @SerializedName("indexesAdded")
    @Expose
    private Integer indexesAdded;
    @SerializedName("indexesRemoved")
    @Expose
    private Integer indexesRemoved;
    @SerializedName("constraintsAdded")
    @Expose
    private Integer constraintsAdded;
    @SerializedName("constraintsRemoved")
    @Expose
    private Integer constraintsRemoved;

    public Integer getNodesCreated() {
        return nodesCreated;
    }

    public void setNodesCreated(Integer nodesCreated) {
        this.nodesCreated = nodesCreated;
    }

    public Integer getNodesDeleted() {
        return nodesDeleted;
    }

    public void setNodesDeleted(Integer nodesDeleted) {
        this.nodesDeleted = nodesDeleted;
    }

    public Integer getRelationshipsCreated() {
        return relationshipsCreated;
    }

    public void setRelationshipsCreated(Integer relationshipsCreated) {
        this.relationshipsCreated = relationshipsCreated;
    }

    public Integer getRelationshipsDeleted() {
        return relationshipsDeleted;
    }

    public void setRelationshipsDeleted(Integer relationshipsDeleted) {
        this.relationshipsDeleted = relationshipsDeleted;
    }

    public Integer getPropertiesSet() {
        return propertiesSet;
    }

    public void setPropertiesSet(Integer propertiesSet) {
        this.propertiesSet = propertiesSet;
    }

    public Integer getLabelsAdded() {
        return labelsAdded;
    }

    public void setLabelsAdded(Integer labelsAdded) {
        this.labelsAdded = labelsAdded;
    }

    public Integer getLabelsRemoved() {
        return labelsRemoved;
    }

    public void setLabelsRemoved(Integer labelsRemoved) {
        this.labelsRemoved = labelsRemoved;
    }

    public Integer getIndexesAdded() {
        return indexesAdded;
    }

    public void setIndexesAdded(Integer indexesAdded) {
        this.indexesAdded = indexesAdded;
    }

    public Integer getIndexesRemoved() {
        return indexesRemoved;
    }

    public void setIndexesRemoved(Integer indexesRemoved) {
        this.indexesRemoved = indexesRemoved;
    }

    public Integer getConstraintsAdded() {
        return constraintsAdded;
    }

    public void setConstraintsAdded(Integer constraintsAdded) {
        this.constraintsAdded = constraintsAdded;
    }

    public Integer getConstraintsRemoved() {
        return constraintsRemoved;
    }

    public void setConstraintsRemoved(Integer constraintsRemoved) {
        this.constraintsRemoved = constraintsRemoved;
    }

}
