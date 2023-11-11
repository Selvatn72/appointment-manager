package com.aptmgt.customer.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SlotRequestDTO {

    @NotNull(message = "ServiceId field should be not empty")
    private Long serviceId;

    private Long branchId;

    private Long employeeId;

    private Long customerId;

    @NotNull(message = "Date field should be not empty")
    private Date date;

    @NotNull(message = "Person count field should be not null")
    private Integer personCount;

    private Boolean isAdmin;
}
