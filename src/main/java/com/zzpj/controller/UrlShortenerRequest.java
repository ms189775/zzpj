package com.zzpj.controller;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UrlShortenerRequest {
    @Size(min = 5, max = 1024, message="URL size must be between {min} and {max} characters long.")
    private String url;
    @Pattern(regexp = "^[a-zA-Z0-9_-]{0,64}$", message="URL name size must be up to {max} characters long and has only A-Z, a-z, 0-9, - and _ characters.")
    private String urlName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }
}
