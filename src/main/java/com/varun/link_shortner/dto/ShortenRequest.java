package com.varun.link_shortner.dto;

public class ShortenRequest {
    private String url;

    public ShortenRequest(String url){
        this.url = url;
    }

    public ShortenRequest(){}

    public void setUrl(String url){
        this.url  = url;
    }

    public String  getUrl(){
        return url;
    }

}
