package com.example.starter.validation;

import com.example.starter.annotation.ValidatePassword;
import lombok.val;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class PasswordConstraintValidator implements ConstraintValidator<ValidatePassword, String> {
    @Override
    public void initialize(ValidatePassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        val validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8,30),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Special, 1)
        ));
        val result = validator.validate(new PasswordData(password));
        return result.isValid();
    }
}
