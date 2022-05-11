package com.mf.starter.service.impl;

import com.mf.starter.config.Constants;
import com.mf.starter.domain.User;
import com.mf.starter.service.UserCacheService;
import com.mf.starter.util.CryptoUtil;
import com.mf.starter.util.TotpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCacheServiceImpl implements UserCacheService {
    private final RedissonClient redisson;
    private final TotpUtil totpUtil;
    private final CryptoUtil cryptoUtil;


    @Override
    public String cacheUser(User user) {
        val mfaId = cryptoUtil.randomAlphanumeric(12);
        log.debug("Generate a mfaId is {}", mfaId);
        RMapCache<String, User> cache = redisson.getMapCache(Constants.CACHE_MFA);
        if (!cache.containsKey(mfaId)) {
            cache.put(mfaId, user, totpUtil.getTimeStepInLong(), TimeUnit.SECONDS);
        }
        return mfaId;
    }

    @Override
    public Optional<User> retrieveUser(String mfaId) {
        RMapCache<String, User> cache= redisson.getMapCache(Constants.CACHE_MFA);
        if (cache.containsKey(mfaId)){
            log.debug("Find relative user info about mfaId {}", mfaId);
            return Optional.of(cache.get(mfaId));
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> verifyTotp(String mfaId, String code) {
        return Optional.empty();
    }
}
