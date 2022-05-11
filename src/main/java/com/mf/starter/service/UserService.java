package com.mf.starter.service;

import com.mf.starter.domain.Auth;
import com.mf.starter.domain.User;

import java.util.Optional;

public interface UserService {
    Auth login(User user);

    boolean isUsernameExisted(String username);

    boolean isEmailExisted(String email);

    boolean isMobileExisted(String mobile);

    User register(User user);

    Optional<User> findOptionalByUsernameAndPassword(String username, String password);

    Optional<String> createTotp(User user);

    Auth loginWithTotp(User user);

    Optional<User> findOptionalByUsername(String username);

}
