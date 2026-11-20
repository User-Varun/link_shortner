package com.varun.link_shortner.services;

import com.varun.link_shortner.dto.ShortenResponse;
import com.varun.link_shortner.dto.UpdateRequest;
import com.varun.link_shortner.exceptions.InvalidArgumentException;
import com.varun.link_shortner.exceptions.RedisException;
import com.varun.link_shortner.exceptions.ShortCodeNotFoundException;
import com.varun.link_shortner.repositories.UrlRepository;
import com.varun.link_shortner.models.UrlModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UrlService {

    private static final Logger log = LoggerFactory.getLogger(UrlService.class);
    
    @Autowired
    private UrlRepository ur;

    @Value("${app.base.url}")
    String baseUrl;

    @Value("${app.base.chars}")
    String chars;

    @Value("${app.base.base}")
    int base;

    @Value("${app.base.cacheExpirationHour}")
    int expirationHours;


    @Autowired
    private RedisTemplate<String , String> redisTemplate;


    @Transactional
    public ShortenResponse shorten(String originalUrl) {

        if (originalUrl == null) {
            throw new InvalidArgumentException("url is Required");
        }

        String trimmed = originalUrl.trim();

        // check if the url exists, if it does,return that.
        UrlModel res = ur.findByOriginalUrl(trimmed);

        if (res != null) {
            return new ShortenResponse(baseUrl + "/" + res.getShortCode() , res.getShortCode());
        }

        // otherwise generate new url
        UrlModel newUrlObj = new UrlModel();

        newUrlObj.setOriginalUrl(trimmed);

        UrlModel obj = ur.save(newUrlObj);

        String shortCode = generateBase62String(obj.getId());

        obj.setShortCode(shortCode);


        UrlModel saved = ur.save(obj);

        return new ShortenResponse(baseUrl + "/" + saved.getShortCode() , saved.getShortCode());
    }

    public String redirect(String shortCode) {

        if (shortCode == null) {
            throw new ShortCodeNotFoundException("Short code not found");}

        String value = null;
        try{
        // first search on cache
        value  = redisTemplate.opsForValue().get(shortCode);
        }catch(DataAccessException e){
            log.warn( e.getMessage()+" Falling back to db");
        }

        if(value != null){
            return value;
        }

        
        // otherwise search on the database
        UrlModel res = ur.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException("Short code not found in the database"));

        try {
            // try to save the cache before returning;
            redisTemplate.opsForValue().set(shortCode, res.getOriginalUrl(), Duration.ofHours(expirationHours));
        }catch(DataAccessException e){
            log.warn(e.getMessage() + " during write, skipping cache update: {}");
        }

        return res.getOriginalUrl();
    }

    public UpdateRequest updateUrl(UpdateRequest req){

        String shortCode = req.getShortCode();
        String newUrl = req.getNewUrl();

        if(newUrl == null || shortCode == null) throw new InvalidArgumentException("url and shortCode is required");

        // check in cache
       String value = redisTemplate.opsForValue().get(shortCode);
        if(value != null){
          // delete it from cache
           redisTemplate.opsForValue().getAndDelete(shortCode);
        }

        // check in db
        UrlModel url = ur.findByShortCode(shortCode)
                .orElseThrow(() -> new ShortCodeNotFoundException("Url not found with the shortCode"));

        // update the url;
        url.setOriginalUrl(newUrl);

        // save the url;
        UrlModel updated = ur.save(url);

        return new UpdateRequest(updated.getShortCode() , updated.getOriginalUrl());
    }

    public void deleteUrl(String shortCode){

        if(shortCode == null) throw new ShortCodeNotFoundException("Short Code not found!");

        redisTemplate.opsForValue().getAndDelete(shortCode);

        ur.deleteByShortCode(shortCode);
    }

    private String generateBase62String(Long id) {

        StringBuilder sb = new StringBuilder();

        // encoder
        do {
            sb.insert(0, chars.charAt((int) (id % base)));
            id /= base;

        } while (id > 0);

        return sb.toString();
    }
}
