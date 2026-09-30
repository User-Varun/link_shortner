package com.varun.link_shortner;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shortner")
public class UrlController {

    @Autowired
    private UrlService us;

    @PostMapping()
    public String shorten(@RequestBody String url){
        return us.shorten(url);
    }

//    @GetMapping("/{code}")
//    public String redirect(@PathVariable String code){
//        return us.redirect(code);
//    }

}
