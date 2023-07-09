package com.example.publictransportationguidance.pojo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Reviews")
public class Review {
    @PrimaryKey
    @NonNull
    private String concatenatedReviewAsId;

    @ColumnInfo(name = "locationName")
    private String locationName;

    @ColumnInfo(name = "destinationName")
    private String destinationName;

    @ColumnInfo(name = "meanOfTransport")
    private String meanOfTransport;

    @ColumnInfo(name = "reviewCase")
    private String reviewCase;

    public String getConcatenatedReviewAsId() {
        return concatenatedReviewAsId;
    }

    public void setConcatenatedReviewAsId(String concatenatedReviewAsId) {
        this.concatenatedReviewAsId = concatenatedReviewAsId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public String getMeanOfTransport() {
        return meanOfTransport;
    }

    public void setMeanOfTransport(String meanOfTransport) {
        this.meanOfTransport = meanOfTransport;
    }

    public String getReviewCase() {
        return reviewCase;
    }

    public void setReviewCase(String reviewCase) {
        this.reviewCase = reviewCase;
    }

    public Review(String locationName, String destinationName, String meanOfTransport, String reviewCase) {
        this.concatenatedReviewAsId=locationName+destinationName+meanOfTransport+reviewCase;
        this.locationName = locationName;
        this.destinationName = destinationName;
        this.meanOfTransport = meanOfTransport;
        this.reviewCase = reviewCase;
    }
}