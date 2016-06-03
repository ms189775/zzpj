package com.zzpj.controller;

import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Controller
public class UrlShortenerController {
    @Autowired
    private StringRedisTemplate redis;

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String showForm(UrlShortenerRequest request) {
        return "home";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void redirectToUrl(@PathVariable String id, HttpServletResponse resp) throws Exception {
        final String url = redis.opsForValue().get(id);
        if (url != null)
            resp.sendRedirect(url);
        else
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @RequestMapping(value="/", method = RequestMethod.POST)
    public ModelAndView shortenUrl(HttpServletRequest httpRequest,
                                   @Valid UrlShortenerRequest request,
                                   BindingResult bindingResult) {
        String url = request.getUrl(); // httpRequest.getParameter("url");
        if (!isUrlValid(url)) {
            String invalidUrlError = "Invalid URL format: " + url + ".";
            bindingResult.addError(new ObjectError("invalidUrlError", invalidUrlError));
        }
        
        String urlName = request.getUrlName();
        if (!urlName.isEmpty() && redis.hasKey(urlName)) {
            String requestUrl = httpRequest.getRequestURL().toString();
            String prefix = requestUrl.substring(0, requestUrl.indexOf(httpRequest.getRequestURI(), "http://".length()));
            String fullUrl = prefix + "/" + urlName;
            String urlExistsError = "URL <a href=\"" + fullUrl + "\" target=\"_blank\">" + fullUrl + "</a> already exists.";
            bindingResult.addError(new ObjectError("urlExistsError", urlExistsError));
        }

        ModelAndView modelAndView = new ModelAndView("home");
        if (!bindingResult.hasErrors()) {
            String id;
            if(!urlName.isEmpty()) {
                id = urlName;
            } else {
                id = Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
            }
            redis.opsForValue().set(id, url);
            String requestUrl = httpRequest.getRequestURL().toString();
            String prefix = requestUrl.substring(0, requestUrl.indexOf(httpRequest.getRequestURI(), "http://".length()));
            String fullUrl = prefix + "/" + id;
            String aliasCreatedInfo = "Alias created: <a href=\"" + fullUrl + "\" target=\"_blank\">" + fullUrl + "</a>!";
            modelAndView.addObject("aliasCreatedInfo", aliasCreatedInfo);
        } else if (bindingResult.getErrorCount() == 1) {
            String error = bindingResult.getAllErrors().get(0).getDefaultMessage();
            modelAndView.addObject("error", error);
        } else {
            String error = "<ul>\n";
            for(ObjectError oe : bindingResult.getAllErrors()) {
                error += "<li>" + oe.getDefaultMessage() + "</li>\n";
            }
            error += "</ul>";
            modelAndView.addObject("error", error);
        }
        return modelAndView;
    }

    private boolean isUrlValid(String url) {
        boolean valid = true;
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            valid = false;
        }
        return valid;
    }
}
