package com.zzpj.service.user;

import com.zzpj.domain.User;
import com.zzpj.domain.UserCreateForm;

import java.util.Collection;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserById(long id);
    Optional<User> getUserByEmail(String email);
    User create(UserCreateForm form);
}