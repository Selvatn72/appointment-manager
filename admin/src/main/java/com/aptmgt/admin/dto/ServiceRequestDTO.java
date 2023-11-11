package com.aptmgt.admin.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequestDTO {

    @NotNull(message = "Branch id field should be not null")
    private List<String> serviceName;

    private Long adminId;

    @NotNull(message = "Service category id field should be not null")
    private Long serviceCategoryId;

    private List<Long> serviceRequestId;

}
