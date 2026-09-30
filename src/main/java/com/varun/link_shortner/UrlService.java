package com.varun.link_shortner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Random;

@Service
public class UrlService {

    @Autowired
    private UrlRepository ur;

    @Transactional
    public String shorten(String url){

        if(url == null) return null;

        UrlModel newUrlObj = new UrlModel();

        newUrlObj.setUrl(url);

        UrlModel obj = ur.save(newUrlObj);


        // 3 digit random no
        int num = new Random().nextInt(90000) + 10000;
        long id = obj.getId();



        String shortCode = generateBase62String(num + id);

        obj.setShortName(shortCode);

        // saving the shortCode
        ur.save(obj);

        //return the url
        return "https://localhost:8080/api/shorten/" + shortCode;
    }

//    public String redirect(String short_name){
//        UrlModel url = ur.findByShort_name(short_name);
//
//        if(url == null) return null;
//
//        return url.getLong_name();
//    }

    private String generateBase62String(Long num){

        final int base = 10;
        final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder sb = new StringBuilder();

        // encoder
        do{
           sb.insert(0 , characters.charAt( (int) (num % base )));
           num /= base;

        }while(num > 0);

        return sb.toString();
    }
}
