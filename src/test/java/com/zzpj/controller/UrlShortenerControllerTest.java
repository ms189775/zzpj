package com.zzpj.controller;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

public class UrlShortenerControllerTest {
    UrlShortenerController usc;
    
    @Before
    public void initObjects() {
        usc = new UrlShortenerController();
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
