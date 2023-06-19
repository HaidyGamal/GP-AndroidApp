package com.example.publictransportationguidance.api;

import static com.example.publictransportationguidance.api.Api.GOOGLE_MAPS_BASE_URL;
import static com.example.publictransportationguidance.api.Api.NEO4J_BASE_URL;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* M Osama: Single Retrofit instance because Retrofit consumes large resources */
public class RetrofitClient {
    public static RetrofitClient instanceForNeo4j =null;
    public static RetrofitClient instanceForGoogleMaps =null;
    private Api api;

    private RetrofitClient(String baseUrl){
        // Increase the timeout duration to 30 seconds.build();
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).client(client).addConverterFactory(GsonConverterFactory.create()).build();
        api=retrofit.create(Api.class);
    }

    public static synchronized RetrofitClient getInstance(String baseUrl){
        if(baseUrl.equals(NEO4J_BASE_URL)){
            if(instanceForNeo4j ==null){ instanceForNeo4j =new RetrofitClient(baseUrl); }
            return instanceForNeo4j;
        }
        else if (baseUrl.equals(GOOGLE_MAPS_BASE_URL)){
            if(instanceForGoogleMaps ==null){ instanceForGoogleMaps =new RetrofitClient(baseUrl); }
            return instanceForGoogleMaps;
        }
        return null;
    }

    public Api getApi(){ return api; }
}
