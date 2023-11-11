package com.aptmgt.admin.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShopHolidayRequestDTO {

    @NotBlank(message = "Shop holiday field should be not empty")
    private String shopHoliday;

    @NotBlank(message = "Description field should be not empty")
    private String description;

    private Long employeeId;

    @NotNull(message = "Branch id field should be not null")
    private Long branchId;

}
