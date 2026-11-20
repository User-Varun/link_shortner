package com.varun.link_shortner.exceptions;

public class RedisException extends RuntimeException{
    public RedisException(String message){
        super(message);
    }
}
