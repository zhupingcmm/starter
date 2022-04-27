package com.mf.starter.util;

import com.mf.starter.config.AppProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtil {


    private static final Long NOW = System.currentTimeMillis();
    public static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final Key REFRESH_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private final AppProperties appProperties;


    public String createJWTToken(UserDetails userDetails, long expireTime, Key signKey) {
        return Jwts.builder()
                .setId("imooc")
                .setSubject(userDetails.getUsername())
                .claim("authorities", userDetails
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
                ).setIssuedAt(new Date(NOW))
                .setExpiration(new Date(NOW + expireTime))
                .signWith(signKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String createJWTToken(UserDetails userDetails, long expireTime) {
        return createJWTToken(userDetails, expireTime, KEY);
    }


    public String createAccessToken(UserDetails userDetails) {
        return createJWTToken(userDetails, appProperties.getJwt().getAccessTokenExpireTime());
    }


    public String createRefreshToken(UserDetails userDetails) {
        return createJWTToken(userDetails, appProperties.getJwt().getRefreshTokenExpireTime(), REFRESH_KEY);
    }
}
