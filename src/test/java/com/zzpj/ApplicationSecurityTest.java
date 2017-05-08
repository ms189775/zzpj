package com.zzpj;

import com.zzpj.config.SecurityConfig;
import com.zzpj.domain.CurrentUser;
import com.zzpj.domain.Link;
import com.zzpj.domain.Role;
import com.zzpj.domain.User;
import com.zzpj.service.link.LinkService;
import com.zzpj.service.user.UserService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.Filter;
import static org.hamcrest.Matchers.containsString;
import org.junit.Before;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    private UserService userService;
    
    @MockBean
    private LinkService linkService;

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
    
    @Test
    public void checkIfRootReturn200() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("URL Shortener")));
    }
    
    @Test
    public void checkIfUsersPageReturn200() throws Exception {
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
        mockMvc.perform(get("/users").with(user(getMockAdmin())))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("<td><a href=\"/user/1\">Testowa nazwa</a></td>")));
    }
    
    @Test
    public void checkIfUsersPageReturn302() throws Exception {
        mockMvc.perform(get("/users").with(user(getMockUser())))
            .andExpect(status().isForbidden());
    }
    
    @Test
    public void checkIfUserPageReturn200() throws Exception {
        Optional<User> optUser = Optional.of(getMockUser().getUser()); 
        when(userService.getUserById(2)).thenReturn(optUser);
        mockMvc.perform(get("/user/2").with(user(getMockUser())))
            .andExpect(status().isOk());
        mockMvc.perform(get("/user/2").with(user(getMockAdmin())))
            .andExpect(status().isOk());
    }
    
    @Test
    public void checkIfUserPageReturn403() throws Exception {
        mockMvc.perform(get("/user/1").with(user(getMockUser())))
            .andExpect(status().isForbidden());
    }
    
    @Test
    public void checkIfExpiredLinkReturn404() throws Exception {
        Link link = new Link();
        link.setUrl("http://test.pl/");
        link.setExpireDate(new Date(Long.MIN_VALUE));
        Optional<Link> optLink = Optional.of(link);
        when(linkService.getLinkByHash("abc")).thenReturn(optLink);
        mockMvc.perform(get("/h/abc"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }
    
    @Test
    public void checkIfUnexpiredLinkReturn302AndRedirect() throws Exception {
        Link link = new Link();
        link.setUrl("http://test.pl/");
        link.setExpireDate(new Date(Long.MAX_VALUE));
        Optional<Link> optLink = Optional.of(link);
        when(linkService.getLinkByHash("abc")).thenReturn(optLink);
        mockMvc.perform(get("/h/abc"))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("http://test.pl/"));
    }
    
    @Test
    public void checkIfRenewPageReturn200() throws Exception {
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
        mockMvc.perform(get("/renew/test").with(user(getMockUser())))
            .andExpect(status().isOk());
        mockMvc.perform(get("/renew/test").with(user(getMockAdmin())))
            .andExpect(status().isOk());
    }
    
    @Test
    public void checkIfRenewPageReturn403() throws Exception {
        CurrentUser currentUser = getMockUser();
        User admin = getMockAdmin().getUser();
        Set<Link> links = new HashSet<>();
        Link link = new Link();
        link.setHash("test2");
        link.setUrl("http://test.pl/");
        links.add(link);
        admin.setLinks(links);
        Optional<User> optUser = Optional.of(admin); 
        when(userService.getUserById(currentUser.getId())).thenReturn(optUser);
        when(linkService.getLinkByHash("test")).thenReturn(Optional.of(link));
        mockMvc.perform(get("/renew/test").with(user(currentUser)))
            .andExpect(status().isForbidden());
    }
}