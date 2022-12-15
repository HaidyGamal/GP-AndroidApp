package com.example.publictransportationguidance.API;

import com.example.publictransportationguidance.API.POJO.ShortestPathResponse.ShortestPath;
import com.example.publictransportationguidance.API.POJO.StopsResponse.AllStops;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {
    /* M Osama: BaseURL used in every Api request */
    String BASE_URL="https://samplepublictransportationsapi.onrender.com/";

    @GET(".")
    Call<AllStops> getAllStops();

    @FormUrlEncoded
    @POST("orderByCost")
    Call<List<List<ShortestPath>>> getShortestByCost(
            @Field("Location") String Location,
            @Field("Destination") String Destination
    );

    @FormUrlEncoded
    @POST("orderByDistance")
    Call<List<List<ShortestPath>>> getShortestByDistance(
            @Field("Location") String Location,
            @Field("Destination") String Destination
    );

}
