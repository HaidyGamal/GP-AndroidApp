package com.example.publictransportationguidance.api;

import com.example.publictransportationguidance.pojo.pathsResponse.NearestPaths;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {
    /* M Osama: BaseURL used in every Api request */
    String BASE_URL="https://tawsila-api.onrender.com/";

    /* M Osama: Request returns available nearest paths orderedBy Cost as default */
    @FormUrlEncoded
    @POST("nearestPaths")
    Call<List<List<NearestPaths>>> getNearestPaths(
            @Field("Location") String Location,
            @Field("Destination") String Destination
    );


/******************** Old Requests ****************************************************/
// M Osama: old request when we were getting locations from our API not Google one's
//    /* M Osama: Request return all the available stops we have in GraphDB */
//    @GET(".")               /* M Osama: RequestURL is the same as BaseURL */
//    Call<AllStops> getAllStops();

// M Osama: Request return all the available paths between two nodes (Ordered by Cost) */
//    @FormUrlEncoded
//    @POST("orderByCost")
//    Call<List<List<NearestPaths>>> getShortestByCost(
//            @Field("Location") String Location,
//            @Field("Destination") String Destination
//    );

// M Osama: Request return all the available paths between two nodes (Ordered by Distance)
//    @FormUrlEncoded
//    @POST("orderByDistance")
//    Call<List<List<NearestPaths>>> getShortestByDistance(
//            @Field("Location") String Location,
//            @Field("Destination") String Destination
//    );
/**************************************************************************************/

}
