package com.aptmgt.customer.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequestDTO {

    private Long id;

    @NotNull(message = "Date field should be not null")
    private Date date;

    @NotNull(message = "StartTime field should be not null")
    private Time startTime;

    @NotNull(message = "Person count field should be not null")
    private Integer personCount;

    private Time endTime;

    private String appointeeName;

    private String appointeeEmail;

    private String appointeePhone;

    private String description;

    private String reasonForCancel;

    @NotNull(message = "BranchId field should be not null")
    private Long branchId;

    private Long employeeId;

    @NotNull(message = "ServiceId field should be not null")
    private Long serviceId;

    @NotNull(message = "CustomerId field should be not null")
    private Long customerId;
}
