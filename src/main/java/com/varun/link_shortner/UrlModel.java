package com.varun.link_shortner;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;

@Entity
@Table(name = "urls")
public class UrlModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String shortName;
    private String url;

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
