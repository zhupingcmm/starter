package com.example.starter.config;


import com.example.starter.security.filter.RestAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;

@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(req -> req
                        .antMatchers("/authorize/login").permitAll()
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .antMatchers("/api/**").hasRole("USER")
                        .anyRequest().authenticated())
                .addFilterAt(restAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf->csrf.disable());
//                .formLogin(form-> form.successHandler(jsonAuthenticateSuccessHandler())
//                        .failureHandler(authenticationFailureHandler()))
//                .httpBasic(Customizer.withDefaults())
//                .logout(logout-> logout.logoutSuccessHandler())
//                .csrf(AbstractHttpConfigurer::disable);
    }

    private RestAuthenticationFilter restAuthenticationFilter() throws Exception {
        RestAuthenticationFilter filter = new RestAuthenticationFilter(objectMapper);
        filter.setAuthenticationSuccessHandler(jsonAuthenticateSuccessHandler());
        filter.setAuthenticationFailureHandler(authenticationFailureHandler());
        filter.setAuthenticationManager(authenticationManager());
        filter.setFilterProcessesUrl("/authorize/login");
        return filter;
    }

    private AuthenticationFailureHandler authenticationFailureHandler() {
        return (req, res, exception) -> {
            val objectMapper = new ObjectMapper();
            val errorData = new HashMap<String, String>();
            errorData.put("title", "failed");
            errorData.put("details", exception.getMessage());
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.getWriter().println(objectMapper.writeValueAsString(errorData));

        };
    }

    private AuthenticationSuccessHandler jsonAuthenticateSuccessHandler() {
        return (req, res, auth) -> {
            val objectMapper = new ObjectMapper();
            val successData = new HashMap<String, String>();
            successData.put("title", "success");
            res.setStatus(HttpStatus.OK.value());
            res.getWriter().println(objectMapper.writeValueAsString(successData));
        };
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user")
                .password(passwordEncoder().encode("12345678"))
                .roles("USER", "ADMIN");
    }

    @Bean
    PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers( "/public/**", "/error")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
