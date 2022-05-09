package com.mf.starter.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Validated
@Configuration
@ConfigurationProperties(prefix = "mooc")
public class AppProperties {
    @Valid
    @Getter
    @Setter
    private Jwt jwt = new Jwt();

    @Setter
    @Getter
    public static class Jwt {
        private String header = "Authorization";
        private String prefix = "Bearer";
        private String jwtId = "imooc";
        private String authorities = "authorities";

        @Min(5000L)
        private long accessTokenExpireTime = 2 * 60 * 1000L; // Access Token 过期时间

        @Min(3600000L)
        private long refreshTokenExpireTime = 30 * 24 * 3600 * 1000L; // Refresh Token 过期时间
    }
}
