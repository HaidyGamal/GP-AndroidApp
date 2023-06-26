
package com.example.publictransportationguidance.pojo.addNewRouteResponse;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Summary {

    @SerializedName("query")
    @Expose
    private Query query;
    @SerializedName("queryType")
    @Expose
    private String queryType;
    @SerializedName("counters")
    @Expose
    private Counters counters;
    @SerializedName("updateStatistics")
    @Expose
    private UpdateStatistics updateStatistics;
    @SerializedName("plan")
    @Expose
    private Boolean plan;
    @SerializedName("profile")
    @Expose
    private Boolean profile;
    @SerializedName("notifications")
    @Expose
    private List<Notification> notifications;
    @SerializedName("server")
    @Expose
    private Server server;
    @SerializedName("resultConsumedAfter")
    @Expose
    private ResultConsumedAfter resultConsumedAfter;
    @SerializedName("resultAvailableAfter")
    @Expose
    private ResultAvailableAfter resultAvailableAfter;
    @SerializedName("database")
    @Expose
    private Database database;

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public Counters getCounters() {
        return counters;
    }

    public void setCounters(Counters counters) {
        this.counters = counters;
    }

    public UpdateStatistics getUpdateStatistics() {
        return updateStatistics;
    }

    public void setUpdateStatistics(UpdateStatistics updateStatistics) {
        this.updateStatistics = updateStatistics;
    }

    public Boolean getPlan() {
        return plan;
    }

    public void setPlan(Boolean plan) {
        this.plan = plan;
    }

    public Boolean getProfile() {
        return profile;
    }

    public void setProfile(Boolean profile) {
        this.profile = profile;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public ResultConsumedAfter getResultConsumedAfter() {
        return resultConsumedAfter;
    }

    public void setResultConsumedAfter(ResultConsumedAfter resultConsumedAfter) {
        this.resultConsumedAfter = resultConsumedAfter;
    }

    public ResultAvailableAfter getResultAvailableAfter() {
        return resultAvailableAfter;
    }

    public void setResultAvailableAfter(ResultAvailableAfter resultAvailableAfter) {
        this.resultAvailableAfter = resultAvailableAfter;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

}
