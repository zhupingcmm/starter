package com.mf.starter.util;

import com.mf.starter.config.AppProperties;
import com.mf.starter.domain.Role;
import com.mf.starter.domain.User;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

@ExtendWith(SpringExtension.class)
public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private AppProperties appProperties;

    @BeforeEach
    public void setUp() {
        appProperties = new AppProperties();
        jwtUtil = new JwtUtil(appProperties);
    }

    @Test
    public void givenUserDetails_thenCreateTokenSuccess() {
        val username = "user";
        val authorities = new HashSet<Role>();

        Role role = Role.builder()
                .authority(Constants.ROLE_ADMIN)
                .build();
        Role role1 = Role.builder()
                .authority(Constants.ROLE_ADMIN)
                .build();
        authorities.add(role);
        authorities.add(role1);

        User user = User.builder()
                .username(username)
                .authorities(authorities)
                .build();

        val token = jwtUtil.createAccessToken(user);
        System.out.println(token);
    }
}
