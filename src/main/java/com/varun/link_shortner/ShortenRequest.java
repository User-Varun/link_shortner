package com.varun.link_shortner;

public class ShortenRequest {
    private String url;

    ShortenRequest(String url){
        this.url = url;
    }

    public void setUrl(String url){
        this.url  = url;
    }

    public String  getUrl(){
        return url;
    }
}
