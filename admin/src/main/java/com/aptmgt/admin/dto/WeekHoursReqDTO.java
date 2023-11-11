package com.aptmgt.admin.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeekHoursReqDTO {

    @NotBlank(message = "Start time field should be not empty")
    private String startTime;

    @NotBlank(message = "End time field should be not empty")
    private String endTime;

    private Long employeeId;

    @NotNull(message = "Branch id field should be not null")
    private Long branchId;

}
