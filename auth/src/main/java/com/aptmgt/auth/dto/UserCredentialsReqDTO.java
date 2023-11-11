package com.aptmgt.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCredentialsReqDTO {

    private String email;

    private String mobileNumber;

    @NotBlank(message = "Password field should be not empty")
    private String password;

}
