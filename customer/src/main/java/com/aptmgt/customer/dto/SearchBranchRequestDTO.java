package com.aptmgt.customer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SearchBranchRequestDTO {

    private String city;

    private String searchName;

    private Long categoryId;

    private Long branchId;

    private String branchName;

    private Long employeeId;

    private Long serviceId;

    private Long subServiceId;

    private Long latitude;

    private Long longitude;
}
