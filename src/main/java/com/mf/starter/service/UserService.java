package com.mf.starter.service;

import com.mf.starter.domain.Auth;
import com.mf.starter.domain.User;

public interface UserService {
    Auth login(String username, String password);


    boolean isUsernameExisted(String username);

    boolean isEmailExisted(String email);

    boolean isMobileExisted(String mobile);

    User register(User user);

}
