package com.varun.link_shortner.dto;

public class UpdateRequest {
    private String newUrl;
    private String shortCode;

    public UpdateRequest(String shortCode ,String newUrl){
        this.shortCode = shortCode;
        this.newUrl = newUrl;
    }

    public String getNewUrl() {
        return newUrl;
    }

    public void setNewUrl(String newUrl) {
        this.newUrl = newUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
}
