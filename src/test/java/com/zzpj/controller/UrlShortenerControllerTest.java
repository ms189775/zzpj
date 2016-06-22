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
    
}
