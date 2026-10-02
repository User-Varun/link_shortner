package com.varun.link_shortner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/shortner")
public class UrlController {

    @Autowired
    private UrlService us;

    @PostMapping()
    public String shorten(@RequestBody ShortenRequest request) {
        return us.shorten(request.getUrl());
    }

    @GetMapping("/{shortCode}")
    public RedirectView redirect(@PathVariable String shortCode) {

        String redirectString = us.redirect(shortCode);

        return new RedirectView(redirectString);
    }

}
