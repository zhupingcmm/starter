package com.mf.starter.rest;

import com.mf.starter.config.AppProperties;
import com.mf.starter.domain.Auth;
import com.mf.starter.domain.dto.LoginDto;
import com.mf.starter.domain.dto.UserDto;
import com.mf.starter.service.UserService;
import com.mf.starter.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/authorize")
@RequiredArgsConstructor
public class AuthorizeResource {
    private final UserService userService;
    private final AppProperties appProperties;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody UserDto userDto) {
        return userDto;
    }

    @PostMapping("/token")
    public Auth login(@Valid @RequestBody LoginDto loginDto) {
        val result = userService.login(loginDto.getUsername(), loginDto.getPassword());
        val accessToken = result.getAccessToken();
        boolean res = jwtUtil.validateAccessToken(accessToken);
        System.out.println(res);
        return result;
    }

    @PostMapping("/token/refresh")
    public Auth refreshToken(@RequestHeader(name = "Authorization") String authorization, @RequestParam String refreshToken) throws AccessDeniedException {
        val accessToken = authorization.replace(appProperties.getJwt().getPrefix(), "").trim();
        if (jwtUtil.validateRefreshToken(refreshToken) && jwtUtil.validateWithoutExpiration(accessToken)) {
            return new Auth(jwtUtil.buildAccessTokenWithRefreshToken(refreshToken), refreshToken);
        }
        throw new AccessDeniedException("Bad Credentials");
    }


}
