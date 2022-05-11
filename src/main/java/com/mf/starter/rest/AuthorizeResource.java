package com.mf.starter.rest;

import com.mf.starter.config.AppProperties;
import com.mf.starter.domain.Auth;
import com.mf.starter.domain.MfaType;
import com.mf.starter.domain.User;
import com.mf.starter.domain.dto.LoginDto;
import com.mf.starter.domain.dto.SendTotpDto;
import com.mf.starter.domain.dto.TotpVerificationDto;
import com.mf.starter.domain.dto.UserDto;
import com.mf.starter.exception.DuplicateProblem;
import com.mf.starter.service.UserCacheService;
import com.mf.starter.service.UserService;
import com.mf.starter.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.Optional;

@RestController
@RequestMapping("/authorize")
@RequiredArgsConstructor
@Slf4j
public class AuthorizeResource {
    private final UserService userService;
    private final AppProperties appProperties;
    private final JwtUtil jwtUtil;
    private final UserCacheService userCacheService;

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
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto) {
        return userService.findOptionalByUsernameAndPassword(loginDto.getUsername(), loginDto.getPassword())
                .map(user -> {
                    //不使用多因子登陆，直接使用用户名密码登陆
                    if (!user.isUsingMfa()) {
                        return ResponseEntity.ok().body(userService.login(user));
                    }
                    //使用多因子登陆
                    val mfaId = userCacheService.cacheUser(user);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .header("X-Authenticate", "mfa", "realm=" + mfaId)
                            .build();

                }).orElseThrow(()-> new BadCredentialsException(""));
    }

    @PostMapping("/token/refresh")
    public Auth refreshToken(@RequestHeader(name = "Authorization") String authorization, @RequestParam String refreshToken) throws AccessDeniedException {
        val accessToken = authorization.replace(appProperties.getJwt().getPrefix(), "").trim();
        if (jwtUtil.validateRefreshToken(refreshToken) && jwtUtil.validateWithoutExpiration(accessToken)) {
            return new Auth(jwtUtil.buildAccessTokenWithRefreshToken(refreshToken), refreshToken);
        }
        throw new AccessDeniedException("Bad Credentials");
    }

    @PutMapping("/totp")
    public void sendTotp(@Valid @RequestBody SendTotpDto sendTotpDto) {
       Optional<Pair<User, String>> result = userCacheService.retrieveUser(sendTotpDto.getMfaId())
                .flatMap(user -> userService.createTotp(user).map(code -> Pair.of(user, code)));
       if (result.isPresent()){
           result.ifPresent(pair -> {
               log.info("totp: {}", pair.getSecond());
               if (sendTotpDto.getMfaType() == MfaType.SMS) {
                   log.info("send mobile service, code is {}", pair.getSecond());
               } else {
                   log.info("send email service, code is {}", pair.getSecond());
               }
           });
       } else {
           throw new RuntimeException("");
       }

    }

    @PostMapping("/totp")
    public Auth verifyTotp(@Valid @RequestBody TotpVerificationDto totpVerificationDto) {
         return userCacheService.verifyTotp(totpVerificationDto.getMfaId(), totpVerificationDto.getCode())
                .map(User::getUsername)
                .flatMap(userService::findOptionalByUsername)
                 .map(userService::loginWithTotp)
                 .orElseThrow(()-> new BadCredentialsException(""));
    }

}
