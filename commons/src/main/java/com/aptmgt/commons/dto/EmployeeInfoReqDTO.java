package com.aptmgt.commons.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EmployeeInfoReqDTO {

    private Long id;

    @NotBlank(message = "Name field should be not empty")
    private String name;

    @Email(message = "Email field should be valid")
    private String email;

    private String phone;

    @NotBlank(message = "Degree field should be not empty")
    private String degree;

    private Integer experience;

    @NotNull(message = "Service Id field should be not empty")
    private Long serviceId;

    @NotNull(message = "Branch Id field should be not empty")
    private Long branchId;

    @NotNull(message = "Sub service Id field should be not empty")
    private List<Long> subServiceId;
}
