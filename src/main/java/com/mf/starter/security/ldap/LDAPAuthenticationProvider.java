package com.mf.starter.security.ldap;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class LDAPAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final LDAPUserRepo ldapUserRepo;
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        return ldapUserRepo
                .findByUsernameAndPassword(username,authentication.getCredentials().toString())
                .orElseThrow(() -> new BadCredentialsException("[LDAP] user or password is wrong!!!"));
    }
}
