package com.mf.starter.annotation;

import com.mf.starter.validation.PasswordMatchValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
@Documented
public @interface ValidatePasswordMatch {
    String message() default "Password not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
