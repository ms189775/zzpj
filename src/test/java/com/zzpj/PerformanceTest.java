package com.zzpj;

import com.zzpj.config.SecurityConfig;
import com.zzpj.controller.UrlShortenerController;
import com.zzpj.domain.CurrentUser;
import com.zzpj.domain.Link;
import com.zzpj.domain.Role;
import com.zzpj.domain.UrlShortenerRequest;
import com.zzpj.domain.User;
import com.zzpj.service.link.LinkService;
import com.zzpj.service.user.UserService;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import javax.servlet.Filter;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class PerformanceTest {
    
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;
    
    private MockMvc mockMvc;
    
    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .addFilters(springSecurityFilterChain)
            .apply(springSecurity())
            .build();
    }

    @MockBean
    private UserService userService;
    
    @MockBean
    private LinkService linkService;
    
    static long _startTime = 0;
    
    public static void start() {
        _startTime = getCpuTimeInMillis();
    }

    public static long stop() {
        long result = (getCpuTimeInMillis() - _startTime);
        _startTime = 0l;
        return result;
    }

    public static boolean isRunning() {
        return _startTime != 0l;
    }

    private static long getCpuTimeInMillis() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() / 10000000 : 0L;
    }
    
    private CurrentUser getMockAdmin() {
        User user = new User();
        user.setId(1l);
        user.setEmail("admin");
        user.setPasswordHash("passAdmin");
        user.setRole(Role.ADMIN);
        return new CurrentUser(user);
    }
    
    private CurrentUser getMockUser() {
        User user = new User();
        user.setId(2l);
        user.setEmail("user");
        user.setPasswordHash("passUser");
        user.setRole(Role.USER);
        return new CurrentUser(user);
    }
    
    @InjectMocks
    private UrlShortenerController controller;
    
    @Test
    public void check100rootPage() throws Exception {
        start();
        for (int i = 0; i < 100; i ++)
        {
            mockMvc.perform(get("/"));
        }
        stop();
        assertTrue(getCpuTimeInMillis() < 3000);
    }

    @Test
    public void check100addLinks() {        
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        
        UrlShortenerRequest request = new UrlShortenerRequest();
        request.setUrl("http://test.pl/");
        request.setUrlName("test");
        
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);
        
        when(linkService.getLinkByHash("test")).thenThrow(new NoSuchElementException());
        
        when(linkService.create("http://test.pl/", "test")).thenReturn(new Link());
        
        start();
        for (int i = 0; i < 100; i ++) {
            controller.shortenUrl(httpServletRequest, request, result);
        }
        stop();
        assertTrue(getCpuTimeInMillis() < 2000);
    }
    
    @Test
    public void check100renews() throws Exception {
        User user = getMockUser().getUser();
        Set<Link> links = new HashSet<>();
        Link link = new Link();
        link.setHash("test");
        link.setUrl("http://test.pl/");
        links.add(link);
        user.setLinks(links);
        Optional<User> optUser = Optional.of(user); 
        when(userService.getUserById(user.getId())).thenReturn(optUser);
        when(linkService.getLinkByHash("test")).thenReturn(Optional.of(link));
                
        start();
        for (int i = 0; i < 100; i ++) {
            mockMvc.perform(get("/renew/test").with(user(getMockUser())));
        }
        stop();
        
        assertTrue(getCpuTimeInMillis() < 1000);
    }
    
    @Test
    public void check100usersPages() throws Exception {
        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setId(1l);
        user.setEmail("Testowa nazwa");
        Set<Link> links = new HashSet<>();
        Link link = new Link();
        link.setUrl("http://test.pl/");
        links.add(link);
        user.setLinks(links);
        userList.add(user);
        when(userService.getUsers()).thenReturn(userList);
        
        start();
        for (int i = 0; i < 100; i ++) {
            mockMvc.perform(get("/users").with(user(getMockAdmin())));
        }
        stop();
        
        assertTrue(getCpuTimeInMillis() < 1000);
    }
    
    @Test
    public void check100userPages() throws Exception {
        Optional<User> optUser = Optional.of(getMockUser().getUser()); 
        when(userService.getUserById(2)).thenReturn(optUser);
        
        start();
        for (int i = 0; i < 100; i ++) {
            mockMvc.perform(get("/user/2").with(user(getMockUser())));
        }
        stop();
        
        assertTrue(getCpuTimeInMillis() < 1000);
    }
    
    @Test
    public void check100redirections() throws Exception {
        Link link = new Link();
        link.setUrl("http://test.pl/");
        link.setExpireDate(new Date(Long.MAX_VALUE));
        Optional<Link> optLink = Optional.of(link);
        when(linkService.getLinkByHash("abc")).thenReturn(optLink);
       
        start();
        for (int i = 0; i < 100; i ++) {
            mockMvc.perform(get("/h/abc"));
        }
        stop();
        
        assertTrue(getCpuTimeInMillis() < 1000);
    }
}
