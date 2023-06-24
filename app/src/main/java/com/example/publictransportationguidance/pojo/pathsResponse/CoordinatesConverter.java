package com.example.publictransportationguidance.pojo.pathsResponse;

import androidx.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CoordinatesConverter {
    @TypeConverter
    public static String fromCoordinates(ArrayList<LatLng> coordinates) {
        Gson gson = new Gson();
        return gson.toJson(coordinates);
    }

    @TypeConverter
    public static ArrayList<LatLng> toCoordinates(String coordinatesJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LatLng>>() {}.getType();
        return gson.fromJson(coordinatesJson, type);
    }
}
