// File: app/src/main/java/com/app/pccooker/network/PincodeResponse.java
package com.app.pccooker.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PincodeResponse {
    @SerializedName("PostOffice")
    public List<PostOffice> PostOffice;

    public static class PostOffice {
        @SerializedName("Name")
        public String Name;

        @SerializedName("District")
        public String District;

        @SerializedName("State")
        public String State;

        @SerializedName("Country")
        public String Country;
    }
}
