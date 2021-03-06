package com.mf.starter.service.impl;

import com.mf.starter.config.Constants;
import com.mf.starter.domain.Auth;
import com.mf.starter.domain.Role;
import com.mf.starter.domain.User;
import com.mf.starter.repository.RoleRepo;
import com.mf.starter.repository.UserRepo;
import com.mf.starter.service.UserService;
import com.mf.starter.util.JwtUtil;
import com.mf.starter.util.TotpUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RoleRepo roleRepo;
    private final TotpUtil totpUtil;


    @Override
    public Auth login(User user) {
        return new Auth(jwtUtil.createAccessToken(user), jwtUtil.createRefreshToken(user));
    }

    @Override
    public boolean isUsernameExisted(String username) {
        return userRepo.countByUsername(username) > 0;
    }

    @Override
    public boolean isEmailExisted(String email) {
        return userRepo.countByEmail(email) > 0;
    }

    @Override
    public boolean isMobileExisted(String mobile) {
        return userRepo.countByMobile(mobile) > 0;
    }

    @Override
    @Transactional
    public User register(User user) {
        return roleRepo.findOptionalByAuthority(Constants.ROLE_USER)
                .map(role -> {
                    Set<Role> roleSet = new HashSet<>();
                    roleSet.add(role);
                    val userToSave = user
                            .withAuthorities(roleSet)
                            .withMfaKey(totpUtil.encodeKeyToString())
                            .withPassword(passwordEncoder.encode(user.getPassword()));
                    return userRepo.save(userToSave);
                }).orElseThrow(() -> new RuntimeException(""));
    }

    @Override
    public Optional<User> findOptionalByUsernameAndPassword(String username, String password) {
        return userRepo.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    @Override
    public Optional<String> createTotp(User user) {
        return totpUtil.createTotp(user.getMfaKey());
    }

    @Override
    public Auth loginWithTotp(User user) {
        val toSave = user.withMfaKey(totpUtil.encodeKeyToString());
        val saved = userRepo.save(toSave);
        return login(saved);
    }

    @Override
    public Optional<User> findOptionalByUsername(String username) {
        return userRepo.findByUsername(username);
    }
}
