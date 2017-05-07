package com.zzpj.controller;

import org.junit.Assert;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
        
@RunWith(SpringRunner.class)
@SpringBootTest
public class UrlShortenerControllerTest {

    @Autowired
    private UrlShortenerController controller;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    /**
     * Test of generateHash method, of class UrlShortenerController.
     */
    @Test
    public void testGenerateHash() {
        String input = "test";
        String expectedOutput = "e5df1276";
        String output = controller.generateHash(input, "1466642618242");
        Assert.assertEquals(expectedOutput, output);
    }
    
    /**
     * Test of isUrlValid method, of class UrlShortenerController.
     */
    @Test
    public void testIsUrlValid() {
        Assert.assertEquals(false, controller.isUrlValid("test"));
        Assert.assertEquals(false, controller.isUrlValid("http://"));
        Assert.assertEquals(false, controller.isUrlValid("http://test"));
        Assert.assertEquals(true, controller.isUrlValid("http://test.pl/"));
    }
    
}
