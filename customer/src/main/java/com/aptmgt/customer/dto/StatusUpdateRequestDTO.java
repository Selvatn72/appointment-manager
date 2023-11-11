package com.aptmgt.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateRequestDTO {

    @NotNull(message = "Appointment Id should not be empty")
    private Long appointmentId;

    private String reasonForCancel;
}
