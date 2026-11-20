package com.varun.link_shortner.dto;



public class ShortenResponse {
    private String url;
    private String shortCode;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public ShortenResponse(String url , String shortCode){
        this.url = url;
        this.shortCode = shortCode;
    }

    public ShortenResponse(){}
}
