package com.example.publictransportationguidance.API;

import com.example.publictransportationguidance.POJO.ShortestPathResponse.ShortestPath;
import com.example.publictransportationguidance.POJO.StopsResponse.AllStops;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {
    /* M Osama: BaseURL used in every Api request */
    String BASE_URL="https://samplepublictransportationsapi.onrender.com/";

    /* M Osama: Request return all the available stops we have in GraphDB */
    @GET(".")               /* M Osama: RequestURL is the same as BaseURL */
    Call<AllStops> getAllStops();

    /* M Osama: Request return all the available paths between two nodes (Ordered by Cost) */
    @FormUrlEncoded
    @POST("orderByCost")
    Call<List<List<ShortestPath>>> getShortestByCost(
            @Field("Location") String Location,
            @Field("Destination") String Destination
    );

    /* M Osama: Request return all the available paths between two nodes (Ordered by Distance) */
    @FormUrlEncoded
    @POST("orderByDistance")
    Call<List<List<ShortestPath>>> getShortestByDistance(
            @Field("Location") String Location,
            @Field("Destination") String Destination
    );

}
