package com.aptmgt.admin.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAvailability {

    private Long serviceAvailabilityId;

    private Long branchId;

    private Long serviceId;

    private String slotTime;

    private String slotDay;

    private Integer slotInterval;

    private Integer slotCapacity;

}
