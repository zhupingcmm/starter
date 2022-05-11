package com.mf.starter.util;

import com.mf.starter.common.BaseIntegrationTest;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
public class TotpUtilTest {



    private TotpUtil totpUtil;

    @BeforeEach
    public void setup() {
        totpUtil = new TotpUtil();
    }

    @Test
    public void testTotp() throws InvalidKeyException {
        val now = Instant.now();
        val validFuture = now.plus(totpUtil.getTimeStep());
        val key = totpUtil.generateKey();
        val first = totpUtil.createTotp(key, now);
        val newKey = totpUtil.generateKey();
        assertTrue(totpUtil.validateTotp(key, first), "第一次验证应该成功");
        val second = totpUtil.createTotp(key, Instant.now());
        assertEquals(first, second, "时间间隔内生成的两个 TOTP 是一致的");
        val afterTimeStep = totpUtil.createTotp(key, validFuture);
        assertNotEquals(first, afterTimeStep, "过期之后和原来的 TOTP 比较应该不一致");
        assertFalse(totpUtil.validateTotp(newKey, first), "使用新的 key 验证原来的 TOTP 应该失败");

    }
}
