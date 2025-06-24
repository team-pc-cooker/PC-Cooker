package com.app.pccooker.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IndiaPostApiService {
    @GET("pincode/{pincode}")
    Call<List<PincodeResponse>> validatePincode(@Path("pincode") String pincode);
}
