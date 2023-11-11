package com.aptmgt.customer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.util.Date;

@Data
public class AppointmentListRequestDTO {

//    @NotNull(message = "CustomerId field should be not empty")
    private Long customerId;

    @NotNull(message = "Date field should be not empty")
    private Date date;

//    @NotNull(message = "StartTime field should be not empty")
    private Time startTime;

    //@NotBlank(message = "Category field should be not empty")
    private String category;
}
