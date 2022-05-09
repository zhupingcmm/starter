package com.mf.starter.service;

import com.mf.starter.domain.Auth;

public interface UserService {
    Auth login(String username, String password);
}
