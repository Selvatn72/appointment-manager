package com.aptmgt.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsReqDTO {

    @Email(message = "Email field should be valid")
    private String email;

    @NotBlank(message = "Password field should be not empty")
    private String password;

}
