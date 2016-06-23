package com.zzpj.controller;

import com.zzpj.domain.Link;
import com.zzpj.domain.User;
import com.zzpj.service.link.LinkService;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

import java.util.Optional;

public class UrlShortenerControllerTest {
    UrlShortenerController usc;
    LinkService ls;
    
    @Before
    public void initObjects() {
        ls = new LinkService() {
            @Override
            public Optional<Link> getLinkByHash(String hash) {
                return null;
            }

            @Override
            public Link create(String url, String hash, User user) {
                return null;
            }

            @Override
            public Link create(String url, String hash) {
                return null;
            }
        };
        usc = new UrlShortenerController(ls);
    }

    /**
     * Test of generateHash method, of class UrlShortenerController.
     */
    @Test
    public void testGenerateHash() {
        String input = "test";
        String expectedOutput = "13d26bba";
        String output = usc.generateHash(input);
        Assert.assertEquals(expectedOutput, output);
    }
    
    /**
     * Test of isUrlValid method, of class UrlShortenerController.
     */
    @Test
    public void testIsUrlValid() {
        Assert.assertEquals(false, usc.isUrlValid("test"));
        Assert.assertEquals(false, usc.isUrlValid("http://"));
        Assert.assertEquals(false, usc.isUrlValid("http://test"));
        Assert.assertEquals(true, usc.isUrlValid("http://test.pl/"));
    }
    
}
