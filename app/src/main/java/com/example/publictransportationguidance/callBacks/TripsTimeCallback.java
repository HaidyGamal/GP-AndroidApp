package com.example.publictransportationguidance;

import com.example.publictransportationguidance.pojo.pathsResponse.NearestPaths;

import java.util.HashMap;
import java.util.List;

public interface TripsTimeCallback {
    void onTripsTimeCalculated(int[] globalDurations);
}
