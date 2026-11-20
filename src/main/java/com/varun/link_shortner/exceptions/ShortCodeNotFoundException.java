package com.varun.link_shortner.exceptions;

public class ShortCodeNotFoundException extends RuntimeException{
    public ShortCodeNotFoundException(String message){
        super(message);
    }
}
