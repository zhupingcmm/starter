package com.mf.starter.config;


import com.mf.starter.security.filter.RestAuthenticationFilter;
import com.mf.starter.security.jwt.JwtFilter;
import com.mf.starter.security.ldap.LDAPAuthenticationProvider;
import com.mf.starter.security.ldap.LDAPUserRepo;
import com.mf.starter.security.userdetails.UserDetailsPasswordServiceImpl;
import com.mf.starter.security.userdetails.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;


import java.util.HashMap;

@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
@Import(SecurityProblemSupport.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ObjectMapper objectMapper;
    private final SecurityProblemSupport securityProblemSupport;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserDetailsPasswordServiceImpl userDetailsPasswordService;
    private final LDAPUserRepo ldapUserRepo;
    private final JwtFilter jwtFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers(req -> req.mvcMatchers("/api/**", "/authorize/**", "/admin/**"))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exp -> exp
                        .authenticationEntryPoint(securityProblemSupport)
                        .accessDeniedHandler(securityProblemSupport))
                .authorizeRequests(req -> req
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        .antMatchers("/api/**").hasRole("USER")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(restAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin(form ->form.disable())
                .csrf(csrf->csrf.disable());
    }

    private RestAuthenticationFilter restAuthenticationFilter() throws Exception {
        RestAuthenticationFilter filter = new RestAuthenticationFilter(objectMapper);
        filter.setAuthenticationSuccessHandler(authenticateSuccessHandler());
        filter.setAuthenticationFailureHandler(authenticationFailureHandler());
        filter.setAuthenticationManager(authenticationManager());
        filter.setFilterProcessesUrl("/authorize/login");
        return filter;
    }

    private AuthenticationFailureHandler authenticationFailureHandler() {
        return (req, res, exception) -> {
            val errorData = new HashMap<String, String>();
            errorData.put("title", "failed");
            errorData.put("details", exception.getMessage());
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.setContentType(MediaType.APPLICATION_JSON_VALUE);
            res.getWriter().println(objectMapper.writeValueAsString(errorData));

        };
    }

    private AuthenticationSuccessHandler authenticateSuccessHandler() {
        return (req, res, auth) -> {
            val successData = new HashMap<String, String>();
            successData.put("title", "success");
            res.setStatus(HttpStatus.OK.value());
            res.getWriter().println(objectMapper.writeValueAsString(successData));
        };
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(new LDAPAuthenticationProvider(ldapUserRepo));

        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .userDetailsPasswordManager(userDetailsPasswordService);
    }



    @Bean
    PasswordEncoder passwordEncoder () {
        val idForDefault = "bcrypt";
        val encoders = new HashMap<String, PasswordEncoder>();
        encoders.put(idForDefault, new BCryptPasswordEncoder());
        encoders.put("SHA-1", new MessageDigestPasswordEncoder("SHA-1"));
        return new DelegatingPasswordEncoder(idForDefault, encoders);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers( "/authorize/**", "/error/**","/h2-console/**");
    }
}
