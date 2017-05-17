package com.zzpj.controller;

import com.zzpj.domain.Role;
import com.zzpj.domain.User;
import com.zzpj.domain.UserCreateForm;
import com.zzpj.service.user.UserService;
import org.junit.Assert;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
        
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationUnitTest {

    @Autowired
    private UrlShortenerController urlShortenerController;
    
    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;
    
    @Test
    public void checkIfContexLoadsIsValid() throws Exception {
        assertThat(urlShortenerController).isNotNull();
    }

    @Test
    public void checkIfGenerateHashIsValid() {
        String input = "test";
        String expectedOutput = "e5df1276";
        String output = urlShortenerController.generateHash(input, "1466642618242");
        Assert.assertEquals(expectedOutput, output);
    }
    
    @Test
    public void checkIfUrlIsValid() {
        Assert.assertEquals(false, urlShortenerController.isUrlValid("test"));
        Assert.assertEquals(false, urlShortenerController.isUrlValid("http://"));
        Assert.assertEquals(false, urlShortenerController.isUrlValid("http://test"));
        Assert.assertEquals(true, urlShortenerController.isUrlValid("http://test.pl/"));
    }
    
    @Test
    public void checkIfGetFullUrlIsValid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("www.test.pl");
        request.setRequestURI("/h/test");
        Assert.assertEquals("http://www.test.pl/h/test", urlShortenerController.getFullUrl(request, "test"));
    }
    
    @Test
    public void checkIfErrorIsThrowWhileRegisteringDuplicateUser() {
        try {
            Authentication existingAuth = new AnonymousAuthenticationToken("test", "mike",
                    AuthorityUtils.createAuthorityList("ROLE_TEST"));
            SecurityContextHolder.getContext().setAuthentication(existingAuth);
            
            UserCreateForm form = new UserCreateForm();
            form.setEmail("test@mail.pl");
            form.setPassword("test");
            form.setRole(Role.USER);

            BindingResult bindingResult = mock(BindingResult.class);
            when(bindingResult.hasErrors()).thenReturn(false);

            when(userService.create(form)).thenThrow(new DataIntegrityViolationException("err"));
            
            Assert.assertEquals("user_create", userController.handleUserCreateForm(form, bindingResult).getViewName());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
