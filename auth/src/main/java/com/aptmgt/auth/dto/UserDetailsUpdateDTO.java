package com.aptmgt.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserDetailsUpdateDTO {

    @NotNull(message = "Id field should be not empty")
    private Long id;

    @NotBlank(message = "Name field should be not empty")
    private String name;

    private String shopName;

    @NotBlank(message = "Phone field should be not empty")
    private String phone;

    private String degree;

    private Integer experience;

    private Long serviceId;

    private Long branchId;

    private List<Long> subServiceId;
}
