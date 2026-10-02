package com.varun.link_shortner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@Service
public class UrlService {

    @Autowired
    private UrlRepository ur;

    @Value("${app.base.url}")
    String baseUrl;

    @Value("${app.base.chars}")
    String chars;

    @Value("${app.base.base}")
    int base;


    @Transactional
    public String shorten(String originalUrl) {

        if (originalUrl == null) {
            return "invalid argument url";
        }

        String trimmed = originalUrl.trim();

        // check if the url exists, if it does,return that.
        UrlModel res = ur.findByOriginalUrl(trimmed);
        if (res != null) {
            return res.getShortCode();
        }

        // otherwise generate new url
        UrlModel newUrlObj = new UrlModel();

        newUrlObj.setOriginalUrl(trimmed);

        UrlModel obj = ur.save(newUrlObj);

        // 3 digit random no
        int num = new Random().nextInt(9000) + 1000;
        long id = obj.getId();

        String shortCode = generateBase62String(num + id);

        obj.setShortCode(shortCode);


        UrlModel saved = ur.save(obj);

        return baseUrl + "/" + saved.getShortCode();
    }

    public String redirect(String shortCode) {

        if (shortCode == null) {
            return null;
        }

        UrlModel res = ur.findByShortCode(shortCode);

        if (res != null) {
            return res.getOriginalUrl();
        }

        return null;
    }

    private String generateBase62String(Long num) {

        StringBuilder sb = new StringBuilder();

        // encoder
        do {
            sb.insert(0, chars.charAt((int) (num % base)));
            num /= base;

        } while (num > 0);

        return sb.toString();
    }
}
