package com.mf.starter.util;

import com.mf.starter.config.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtUtil {


    public static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final Key REFRESH_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private final AppProperties appProperties;


    public String createJWTToken(UserDetails userDetails, long expireTime, Key signKey) {
        return Jwts.builder()
                .setId(appProperties.getJwt().getJwtId())
                .setSubject(userDetails.getUsername())
                .claim(appProperties.getJwt().getAuthorities(), userDetails
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
                ).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
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

    public boolean validateAccessToken(String token) {
        return validateToken(token, KEY);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, REFRESH_KEY);
    }

    public boolean validateToken(String jwtToken, Key signKey) {
        try {
            Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateWithoutExpiration(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(JwtUtil.KEY).build().parseClaimsJws(jwtToken);
            return true;
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return e instanceof ExpiredJwtException;
        }
    }

    public String buildAccessTokenWithRefreshToken(String jwtToken) {
        return parseClaims(jwtToken, REFRESH_KEY)
                .map(claims -> Jwts.builder()
                        .setClaims(claims)
                        .setExpiration(new Date(System.currentTimeMillis() + appProperties.getJwt().getAccessTokenExpireTime()))
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .signWith(KEY, SignatureAlgorithm.HS512)
                        .compact()
                ).orElseThrow(() -> new BadCredentialsException("Failed build access token"));
    }

    public Optional<Claims> parseClaims(String jwtToken, Key signKey) {
        try {
            val claims = Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(jwtToken).getBody();
            return Optional.of(claims);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
