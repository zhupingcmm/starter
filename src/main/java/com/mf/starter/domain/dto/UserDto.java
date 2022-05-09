package com.mf.starter.domain.dto;

import com.mf.starter.annotation.ValidateEmail;
import com.mf.starter.annotation.ValidatePassword;
import com.mf.starter.annotation.ValidatePasswordMatch;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@ValidatePasswordMatch
public class UserDto implements Serializable {
    @NotNull
    @NotBlank
    @Size(min = 4, max = 50, message = "user name size must be in 4~50")
    private String username;

    @NotNull
    @ValidatePassword
    private String password;

    @NotNull
    @Size(min = 8, max = 20, message = "password  size must be in 8~20")
    private String matchingPassword;

    @ValidateEmail
    @NotNull
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 4, max = 50, message = "name size must be in 4~50")
    private String name;

    @NotNull
    @NotBlank
    @Size(min = 11, max = 11, message = "mobile is 11")
    private String mobile;

}
