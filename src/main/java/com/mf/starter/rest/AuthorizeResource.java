package com.mf.starter.rest;

import com.mf.starter.config.AppProperties;
import com.mf.starter.domain.Auth;
import com.mf.starter.domain.User;
import com.mf.starter.domain.dto.LoginDto;
import com.mf.starter.domain.dto.UserDto;
import com.mf.starter.exception.DuplicateProblem;
import com.mf.starter.service.UserService;
import com.mf.starter.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
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
    public void register(@Valid @RequestBody UserDto userDto) {
        if (userService.isUsernameExisted(userDto.getUsername())) {
            throw new DuplicateProblem("Exception.duplicate.username");
        }
        if (userService.isEmailExisted(userDto.getEmail())) {
            throw new DuplicateProblem("Exception.duplicate.email");
        }

        if (userService.isMobileExisted(userDto.getMobile())) {
            throw new DuplicateProblem("Exception.duplicate.mobile");
        }

        User user = User.builder()
                .username(userDto.getUsername())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .mobile(userDto.getMobile())
                .password(userDto.getPassword())
                .build();

        userService.register(user);
    }

    @PostMapping("/token")
    public Auth login(@Valid @RequestBody LoginDto loginDto) {
        return userService.login(loginDto.getUsername(), loginDto.getPassword());
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
