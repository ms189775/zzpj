package com.zzpj.controller;

import org.junit.Assert;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
        
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationUnitTest {

    @Autowired
    private UrlShortenerController controller;

    @Test
    public void checkIfContexLoadsIsValid() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    public void checkIfGenerateHashIsValid() {
        String input = "test";
        String expectedOutput = "e5df1276";
        String output = controller.generateHash(input, "1466642618242");
        Assert.assertEquals(expectedOutput, output);
    }
    
    @Test
    public void checkIfUrlIsValid() {
        Assert.assertEquals(false, controller.isUrlValid("test"));
        Assert.assertEquals(false, controller.isUrlValid("http://"));
        Assert.assertEquals(false, controller.isUrlValid("http://test"));
        Assert.assertEquals(true, controller.isUrlValid("http://test.pl/"));
    }
    
    @Test
    public void checkIfGetFullUrlIsValid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("www.test.pl");
        request.setRequestURI("/h/test");
        Assert.assertEquals("http://www.test.pl/h/test", controller.getFullUrl(request, "test"));
    }
}
