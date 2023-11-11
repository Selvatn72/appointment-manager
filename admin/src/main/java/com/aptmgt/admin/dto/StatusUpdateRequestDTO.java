package com.aptmgt.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StatusUpdateRequestDTO {

    @NotNull(message = "Appointment Id field should not be null")
    private Long appointmentId;

    private Integer statusId;

    private String reasonForCancel;

}
