package com.example.publictransportationguidance.API;

import com.example.publictransportationguidance.API.POJO.AllNodes;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Api {
    String BASE_URL="https://public-transportations.herokuapp.com/"; /* Api doc: BaseURL used in every Api request */

    /* GET request (rootThatWillBeAddedToBaseURL) */
    /* It returns with JSON array of Hero; so we stores it in List<Hero> */
    @GET(".")           /* GET request URL = Base URL */
    Call<AllNodes> getConstantValue();
}
