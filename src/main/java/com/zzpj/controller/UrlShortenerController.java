package com.zzpj.controller;

import com.google.common.hash.Hashing;
import dao.LinkDao;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.nio.charset.StandardCharsets;
import org.apache.commons.validator.UrlValidator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

@Controller
public class UrlShortenerController {
    LinkDao linkDao;
    
    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(value="/", method = RequestMethod.GET)
    public String showForm(UrlShortenerRequest request) {
        return "home";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public void redirectToUrl(@PathVariable String id, HttpServletResponse resp) throws Exception {
        String confFile = "applicationContext.xml";
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(confFile);
        linkDao = (LinkDao) context.getBean("linkDAO");
        final String url = linkDao.findFullLink(id);
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
        /*if (!urlName.isEmpty() && redis.hasKey(urlName)) {
            String requestUrl = httpRequest.getRequestURL().toString();
            String prefix = requestUrl.substring(0, requestUrl.indexOf(httpRequest.getRequestURI(), "http://".length()));
            String fullUrl = prefix + "/" + urlName;
            String urlExistsError = "URL <a href=\"" + fullUrl + "\" target=\"_blank\">" + fullUrl + "</a> already exists.";
            bindingResult.addError(new ObjectError("urlExistsError", urlExistsError));
        }*/

        ModelAndView modelAndView = new ModelAndView("home");
        if (!bindingResult.hasErrors()) {
            String id;
            if(!urlName.isEmpty()) {
                id = urlName;
            } else {
                id = generateHash(url);
            }
            linkDao.setLink(id, url);
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

    protected String generateHash(String url)
    {
        return Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
    }
    
    protected boolean isUrlValid(String url) {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes);
        return urlValidator.isValid(url);
    }
}
