package com.app.pccooker.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    private static Retrofit amazonRetrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.postalpincode.in/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    public static AmazonApiService getAmazonApiService() {
        if (amazonRetrofit == null) {
            amazonRetrofit = new Retrofit.Builder()
                    .baseUrl("https://your-amazon-api-endpoint.com/") // Replace with actual Amazon API endpoint
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return amazonRetrofit.create(AmazonApiService.class);
    }
}
