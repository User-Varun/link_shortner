package com.varun.link_shortner;

public class ShortenRequestDto {
    String url;

    ShortenRequestDto(String url){
        this.url = url;
    }

    public void setUrl(String url){
        this.url  = url;
    }
}
