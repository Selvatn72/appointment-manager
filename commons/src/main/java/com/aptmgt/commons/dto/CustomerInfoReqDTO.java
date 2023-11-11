package com.aptmgt.commons.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class CustomerInfoReqDTO {

    @NotBlank(message = "Name field should be not empty")
    private String name;

    @Email(message = "Email field should be valid")
    private String email;

    @NotBlank(message = "Phone field should be not empty")
    private String phone;

    @NotBlank(message = "Password field should be not empty")
    private String password;

}
