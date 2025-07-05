package com.app.pccooker.network;

import com.app.pccooker.models.IndiaPostResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IndiaPostApiService {
    @GET("pincode/{pincode}")
    Call<List<IndiaPostResponse>> getLocationDetails(@Path("pincode") String pincode);
}
