package com.app.pccooker.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PincodeUtils {

    public interface PincodeCallback {
        void onResult(String city, String state);
    }

    public static void lookupPincode(String pincode, Context context, PincodeCallback callback) {
        String url = "https://api.postalpincode.in/pincode/" + pincode;

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject(0);
                        String status = jsonObject.getString("Status");
                        
                        if (status.equals("Success")) {
                            JSONArray postOffices = jsonObject.getJSONArray("PostOffice");

                            if (postOffices.length() > 0) {
                                JSONObject firstOffice = postOffices.getJSONObject(0);
                                String city = firstOffice.getString("District");
                                String state = firstOffice.getString("State");
                                
                                // Check if the state is Andhra Pradesh
                                if ("Andhra Pradesh".equalsIgnoreCase(state)) {
                                    callback.onResult(city, state);
                                } else {
                                    // For other states, return special indicator
                                    callback.onResult("OTHER_STATE", state);
                                }
                            } else {
                                callback.onResult(null, null);
                            }
                        } else {
                            callback.onResult(null, null);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onResult(null, null);
                    }
                },
                error -> {
                    error.printStackTrace();
                    callback.onResult(null, null);
                });

        queue.add(jsonArrayRequest);
    }
    public static List<String> loadAndhraCities(Context context) {
        List<String> cities = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open(".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject obj = new JSONObject(json);
            JSONArray array = obj.getJSONArray("cities");
            for (int i = 0; i < array.length(); i++) {
                cities.add(array.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cities;
    }

}