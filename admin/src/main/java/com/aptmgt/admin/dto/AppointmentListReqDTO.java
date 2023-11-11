package com.aptmgt.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class AppointmentListReqDTO {

    private Long branchId;

    private Long employeeId;

    private Date date;

    private Date startDate;

    private Date endDate;

    private Integer statusId;

    private String mobileNumber;

    @NotNull(message = "Page number field should not be empty")
    private Integer pageNo;

    @NotNull(message = "Page size field should not be empty")
    private Integer pageSize;
}
