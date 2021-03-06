package com.zzpj.controller;

import com.zzpj.domain.CurrentUser;
import com.zzpj.domain.Link;
import com.zzpj.domain.Role;
import com.zzpj.domain.User;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrentUserControllerAdvice.class);

    @ModelAttribute("currentUser")
    public CurrentUser getCurrentUser(Authentication authentication) {
        return (authentication == null) ? null : (CurrentUser) authentication.getPrincipal();
    }
}