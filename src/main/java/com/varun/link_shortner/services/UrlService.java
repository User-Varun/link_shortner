package com.varun.link_shortner.services;

import com.varun.link_shortner.dto.ShortenResponse;
import com.varun.link_shortner.dto.UpdateRequest;
import com.varun.link_shortner.exceptions.InvalidArgumentException;
import com.varun.link_shortner.exceptions.ShortCodeNotFoundException;
import com.varun.link_shortner.repositories.UrlRepository;
import com.varun.link_shortner.models.UrlModel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService {


    private final  UrlRepository ur;

    private final RedisTemplate<String , String> redisTemplate;

    @Value("${app.base.url}")
    String baseUrl;

    @Value("${app.base.chars}")
    String chars;

    @Value("${app.base.base}")
    int base;

    @Value("${app.base.cacheExpirationHour}")
    int expirationHours;

    @Value("${app.base.urlCounterRedis}")
    long ID_COUNTER;

    @Value("${app.base.counterKey")
    String COUNTER_KEY;

    @Value("${app.base.redisCounterOffset}")
    String COUNTER_OFFSET;



    @PostConstruct
    public void initCounter() {
        redisTemplate.opsForValue().setIfAbsent(COUNTER_KEY , String.valueOf(ID_COUNTER));
    }


    @Transactional
    public ShortenResponse shorten(String originalUrl) {

        if (originalUrl == null) {
            throw new InvalidArgumentException("url is Required");
        }

        String trimmed = originalUrl.trim();
        String normalizedUrl = normalizeUrl(trimmed);

        // check if the url exists, if it does,return that.
        UrlModel res = ur.findByOriginalUrl(normalizedUrl);

        if (res != null) {
            return new ShortenResponse(baseUrl + "/" + res.getShortCode() , res.getShortCode());
        }

        // otherwise generate new url
        Long uniqueId = redisTemplate.opsForValue().increment(COUNTER_KEY);

        if(uniqueId == null) throw new ShortCodeNotFoundException("failed to generate unique id from redis");

        UrlModel newUrlObj = new UrlModel();

        String shortCode = encode(uniqueId);

        newUrlObj.setOriginalUrl(normalizedUrl);
        newUrlObj.setShortCode(shortCode);

        UrlModel saved = ur.save(newUrlObj);

        // add to the redis cache
        try{
            redisTemplate.opsForValue().set(saved.getShortCode() , saved.getOriginalUrl() , Duration.ofHours(expirationHours));
        }catch(DataAccessException ex){
            log.warn(ex.getMessage() + " during write, skipping cache update: {}");
        }

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
            return normalizeUrl(value);
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

        return normalizeUrl(res.getOriginalUrl());
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


    /**
     * Generates a fixed-length Base62 short code by combining the atomic Redis counter ID
     * with a configured offset. Configuration values ({@code base}, {@code chars}, and
     * {@code offset}) are loaded from {@code application.properties}.
     *
     * <ul>
     *      <li>offset :- bigger number (62 ^ 6)  to generate 7 char shortcodes </li>
     *      <li>chars :- the jumbled base62 string, used for readability.</li>
     *      <li>base :- used 62 this should match with the {@code chars.length } for consistent shortCode generation</li>
     * </ul>
     *
     *
     * @param redisId id generated by the redis counter atomically.
     */
    private String encode(long redisId) {

        long uniqueId = Long.parseLong(COUNTER_OFFSET) + redisId;

        StringBuilder sb = new StringBuilder(7);

        // encoder
        do {
            sb.insert(0, chars.charAt((int) (uniqueId % base)));
            uniqueId /= base;

        } while (uniqueId > 0);

        return sb.toString();
    }

    /**
     * returns url in format "https://url-here" lowercased
     *
     * @param url normalized url
     */
    private String normalizeUrl(String url) {
        String trimmedUrl = url.trim();
        String lowerCaseUrl = trimmedUrl.toLowerCase();

        if (lowerCaseUrl.startsWith("http://") || lowerCaseUrl.startsWith("https://")) {
            return trimmedUrl;
        }

        return "https://" + trimmedUrl;
    }
}
