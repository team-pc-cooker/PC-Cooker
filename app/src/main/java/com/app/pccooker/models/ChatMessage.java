package com.app.pccooker.models;

public class ChatMessage {
    private String message;
    private boolean isFromUser;
    private long timestamp;
    private String responseType; // AI response type: build, suggest, help, error, etc.
    
    public ChatMessage(String message, boolean isFromUser) {
        this.message = message;
        this.isFromUser = isFromUser;
        this.timestamp = System.currentTimeMillis();
        this.responseType = null;
    }
    
    public ChatMessage(String message, boolean isFromUser, long timestamp) {
        this.message = message;
        this.isFromUser = isFromUser;
        this.timestamp = timestamp;
        this.responseType = null;
    }
    
    public ChatMessage(String message, boolean isFromUser, String responseType) {
        this.message = message;
        this.isFromUser = isFromUser;
        this.timestamp = System.currentTimeMillis();
        this.responseType = responseType;
    }
    
    public ChatMessage(String message, boolean isFromUser, long timestamp, String responseType) {
        this.message = message;
        this.isFromUser = isFromUser;
        this.timestamp = timestamp;
        this.responseType = responseType;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isFromUser() {
        return isFromUser;
    }
    
    public void setFromUser(boolean fromUser) {
        this.isFromUser = fromUser;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getResponseType() {
        return responseType;
    }
    
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }
} 