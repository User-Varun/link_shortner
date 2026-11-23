package com.varun.link_shortner.exceptions;

public class ShortCodeGenerationFailedException extends RuntimeException{

    public ShortCodeGenerationFailedException(String message){
        super(message);
    }
}
