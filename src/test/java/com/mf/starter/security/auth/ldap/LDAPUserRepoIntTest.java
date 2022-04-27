package com.mf.starter.security.auth.ldap;

import com.mf.starter.security.ldap.LDAPUserRepo;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DataJdbcTest
public class LDAPUserRepoIntTest {

    @Autowired
    private LDAPUserRepo ldapUserRepo;

    @BeforeEach
    public void setUp() {

    }
    @Test
    public void givenUsernameAndPasswordThenSuccess() {
        val user = ldapUserRepo.findByUsernameAndPassword("zhaoliu", "123");
        System.out.println(user);
    }


}
