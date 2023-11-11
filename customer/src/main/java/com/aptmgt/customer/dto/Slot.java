package com.aptmgt.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Slot {

    private Time slotTime;

    private Boolean isBooked = false;

    private String status;

    private Integer availableSlot;
}
