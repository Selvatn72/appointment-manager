package com.aptmgt.auth.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AdminInfoRequestDTO {

    @NotBlank(message = "Name field should be not empty")
    private String name;

    @Email(message = "Email field should be valid")
    private String email;

    @NotBlank(message = "Phone field should be not empty")
    private String phone;

    @NotBlank(message = "Password field should be not empty")
    private String password;

    private String shopName;

    @NotNull(message = "Service category Id field should be not empty")
    private Long serviceCategoryId;
}
