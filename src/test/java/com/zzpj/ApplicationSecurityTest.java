package com.zzpj;

import com.zzpj.config.SecurityConfig;
import com.zzpj.domain.CurrentUser;
import com.zzpj.domain.Link;
import com.zzpj.domain.Role;
import com.zzpj.domain.User;
import com.zzpj.service.user.UserService;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.hamcrest.Matchers.containsString;
import org.junit.Before;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
public class ApplicationSecurityTest {

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
    private UserService service;

    private CurrentUser getUser() {
        User user = new User();
        user.setId(1l);
        user.setEmail("user");
        user.setPasswordHash("pass");
        user.setRole(Role.USER);
        return new CurrentUser(user);
    }
    
    private CurrentUser getAdmin() {
        User user = new User();
        user.setId(1l);
        user.setEmail("admin");
        user.setPasswordHash("pass");
        user.setRole(Role.ADMIN);
        return new CurrentUser(user);
    }
    
    @Test
    public void rootShouldReturn200andCorrectContent() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("URL Shortener")));
    }
    
    @Test
    public void usersShouldReturn200() throws Exception {
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
        when(service.getUsers()).thenReturn(userList);

        mockMvc.perform(get("/users").with(user(getAdmin())))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("<td><a href=\"/user/1\">Testowa nazwa</a></td>")));
    }
    
    @Test
    public void usersShouldReturn403() throws Exception {
        mockMvc.perform(get("/users").with(user(getUser())))
            .andDo(print())
            .andExpect(status().isForbidden());
    }
    
    /*@Test
    public void loginUser1Ok() throws Exception {		
        RequestBuilder requestBuilder = formLogin().user("admin").password("123");
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(cookie().exists("remember-me"));				
    }*/
    
    /*@Test
    public void greetingShouldReturnMessageFromService() throws Exception {
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(httpServletRequest.getRequestURI()).thenReturn("http://test.pl/");
        Link link = new Link();
        link.setUrl("http://test.pl/");
        LocalDateTime ldt = LocalDateTime.now().plusDays(7);
        Instant instant = ldt.toInstant(ZoneOffset.UTC);
        link.setExpireDate(Date.from(instant));
        Optional<Link> optLink = Optional.of(link); 
        when(service.getLinkByHash("hash")).thenReturn(optLink);
        
        this.mockMvc.perform(get("/h/hash")).andExpect(status().isOk());
                //.andExpect(redirectedUrlPattern("http://test.pl/"));
    }*/
}