package com.aptmgt.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlotResponse {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private String day;

    private Integer slotCapacity;

    private Boolean isHoliday = false;

    private List<Slot> slots;
}
