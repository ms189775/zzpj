package com.zzpj.service.currentUser;

import com.zzpj.domain.CurrentUser;
import com.zzpj.domain.Link;
import com.zzpj.domain.Role;
import com.zzpj.domain.User;
import com.zzpj.repository.UserRepository;
import com.zzpj.service.user.UserService;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {
    private final UserService userService;

    @Autowired
    public CurrentUserServiceImpl(UserService userService) {
        this.userService = userService;
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserDetailsService.class);

    @Override
    public boolean canAccessUser(CurrentUser currentUser, Long userId) {
        LOGGER.debug("Checking if user={} has access to user={}", currentUser, userId);
        return currentUser != null
                && (currentUser.getRole() == Role.ADMIN || currentUser.getId().equals(userId));
    }
    
    @Override
    public boolean canAccessLink(CurrentUser currentUser, String hash) {
        LOGGER.debug("Checking if user={} has access to link={}", currentUser, hash);
        return currentUser != null
                && (currentUser.getRole() == Role.ADMIN || containsHash(userService.getUserById(currentUser.getId()).get().getLinks(), hash));
    }
    
    public static boolean containsHash(Set<Link> links, String hash) {
        for (Link link : links) {
            if (link.getHash().equals(hash)) {
                return true;
            }
        }
        return false;
    }
}