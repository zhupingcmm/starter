package com.mf.starter.validation;

import com.mf.starter.annotation.ValidateEmail;
import lombok.val;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidateEmail,String> {
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.([a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?){2,})$";
    @Override
    public void initialize(ValidateEmail constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return validateEmail(value);
    }

    private boolean validateEmail (String email) {
        val pattern = Pattern.compile(EMAIL_PATTERN);
        val matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
