package com.app.pccooker.models;

import java.util.List;

public class IndiaPostResponse {
    public String Status;
    public List<PostOffice> PostOffice;

    public static class PostOffice {
        public String Name;
        public String District;
        public String State;
    }
}
