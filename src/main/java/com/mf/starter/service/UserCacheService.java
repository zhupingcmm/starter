package com.mf.starter.service;

import com.mf.starter.domain.User;

import java.util.Optional;

public interface UserCacheService {
    String cacheUser(User user);

    Optional<User> retrieveUser(String mfaId);

    Optional<User> verifyTotp(String mfaId, String code);
}
