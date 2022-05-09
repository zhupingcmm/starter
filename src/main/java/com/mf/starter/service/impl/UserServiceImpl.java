package com.mf.starter.service.impl;

import com.mf.starter.domain.Auth;
import com.mf.starter.repository.UserRepo;
import com.mf.starter.service.UserService;
import com.mf.starter.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public Auth login(String username, String password) {
        return userRepo.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> new Auth(jwtUtil.createAccessToken(user), jwtUtil.createRefreshToken(user)))
                .orElseThrow(() -> new AccessDeniedException("Username or password is wrong"));
    }
}
