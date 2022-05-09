package com.mf.starter.security.jwt;

import com.mf.starter.config.AppProperties;
import com.mf.starter.util.CollectionUtil;
import com.mf.starter.util.JwtUtil;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final AppProperties appProperties;
    private final JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (checkJwtToken(request)) {
           Optional<Claims> optional =  validateToken(request)
                    .filter(claims -> claims.get("authorities") != null);
           if (optional.isPresent()) {
                setupSpringAuthentication(optional.get());
           } else {
               SecurityContextHolder.clearContext();
           }
        }

        filterChain.doFilter(request, response);
    }

    private void setupSpringAuthentication(Claims claims) {
        val rawList = CollectionUtil.convertObjectToList(claims.get("authorities"));
        val authorities = rawList.stream()
                .map(String::valueOf)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        val authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }
    private Optional<Claims> validateToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(appProperties.getJwt().getHeader())
                                    .replace(appProperties.getJwt().getPrefix(), "").trim();
        try {
            jwtUtil.validateAccessToken(jwtToken);
          return Optional.of(Jwts.parserBuilder().setSigningKey(JwtUtil.KEY).build().parseClaimsJws(jwtToken).getBody());
        }catch (ExpiredJwtException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e){
            return Optional.empty();
        }
    }


    private boolean checkJwtToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(appProperties.getJwt().getHeader());
        return authenticationHeader != null && authenticationHeader.startsWith(appProperties.getJwt().getPrefix());
    }
}
