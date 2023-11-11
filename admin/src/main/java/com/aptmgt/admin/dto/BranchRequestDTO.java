package com.aptmgt.admin.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchRequestDTO {

    @NotBlank(message = "Name field should be not empty")
    private String name;

    @NotBlank(message = "Phone field should be not empty")
    private String phone;

    @NotBlank(message = "Mail field should be not empty")
    private String mail;

    @NotBlank(message = "Address field should be not empty")
    private String address;

    private String website;

    @NotBlank(message = "City field should be not empty")
    private String city;

    @NotBlank(message = "State field should be not empty")
    private String state;

    @NotBlank(message = "Country field should be not empty")
    private String country;

    @NotBlank(message = "Zipcode field should be not empty")
    private String zipcode;

    @NotNull(message = "Admin id field should be not null")
    private Long adminId;

    @NotNull(message = "Service category id field should be not null")
    private Long serviceCategoryId;

}
