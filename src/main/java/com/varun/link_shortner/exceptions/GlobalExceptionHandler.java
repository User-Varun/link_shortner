package com.varun.link_shortner.exceptions;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "com.varun.link_shortner.controllers")
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidArgumentException.class)
    public ResponseEntity<String> handleInvalidArgumentException(InvalidArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<String> handleShortCodeNotFound(ShortCodeNotFoundException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(RedisException.class)
    public ResponseEntity<String> handleRedisException(RedisException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ShortCodeGenerationFailedException.class)
    public ResponseEntity<String> handleShortCodeGenFailed(ShortCodeGenerationFailedException ex){
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}
