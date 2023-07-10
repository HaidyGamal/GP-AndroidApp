package com.example.publictransportationguidance.api;

import com.example.publictransportationguidance.pojo.addNewRouteResponse.AddNewRoute;
import com.example.publictransportationguidance.pojo.estimatedTimeResponse.EstimatedTime;
import com.example.publictransportationguidance.pojo.pathsResponse.NearestPaths;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Api {
    /* M Osama: BaseURL used in every Api request */
    String NEO4J_BASE_URL ="https://tawsila-api.onrender.com/";
    String GOOGLE_MAPS_BASE_URL ="https://maps.googleapis.com/";

    /* M Osama: Request returns available nearest paths orderedBy Cost as default */
    @FormUrlEncoded
    @POST("nearestPaths")
    Call<List<List<NearestPaths>>> getNearestPaths(
            @Field("Location") String Location,
            @Field("Destination") String Destination
    );

    @FormUrlEncoded
    @POST("addNewRoute")
    Call<AddNewRoute> addNewRoute(
            @Field("Location") String Location,
            @Field("Destination") String Destination,
            @Field("Cost") String Cost,
            @Field("LineName") String LineName,
            @Field("Type") String Type
    );

    @GET("maps/api/directions/json")
    Call<EstimatedTime> getEstimatedTime(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("mode") String mode,
            @Query("transit_mode") String transit_mode,
            @Query("key") String apiKey
    );

}
