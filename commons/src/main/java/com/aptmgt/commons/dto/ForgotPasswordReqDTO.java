package com.aptmgt.commons.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class ForgotPasswordReqDTO {

        @Email(message = "Email field should be valid")
        private String email;

        private String verificationCode;
}
