package com.aptmgt.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlotDetailsDTO {

    private Long serviceId;

    private String serviceName;

    private String slotTime;

    private String slotDay;

    private Integer slotInterval;

    private Integer slotCapacity;

    private Long serviceAvailabilityId;
}
