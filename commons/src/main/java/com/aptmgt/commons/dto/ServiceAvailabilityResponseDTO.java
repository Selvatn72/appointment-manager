package com.aptmgt.commons.dto;

import com.aptmgt.commons.model.ServiceAvailabilityEntity;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAvailabilityResponseDTO {

    private Long serviceAvailabilityId;

    private Long employeeId;

    private Long branchId;

    private Long serviceId;

    private String serviceName;

    private String slotTime;

    private String slotDay;

    private Integer slotInterval;

    private Integer slotCapacity;

    public ServiceAvailabilityResponseDTO(ServiceAvailabilityEntity serviceAvailabilityEntity) {
        this.serviceAvailabilityId = serviceAvailabilityEntity.getServiceAvailabilityId();
        this.branchId = serviceAvailabilityEntity.getBranchId().getBranchId();
        this.employeeId = serviceAvailabilityEntity.getEmployeeId() != null ? serviceAvailabilityEntity.getEmployeeId().getEmployeeId() : null;
        this.serviceId = serviceAvailabilityEntity.getServiceId().getServiceId();
        this.serviceName = serviceAvailabilityEntity.getServiceId().getServiceName();
        this.slotTime = serviceAvailabilityEntity.getSlotTime();
        this.slotDay = serviceAvailabilityEntity.getSlotDay();
        this.slotInterval = serviceAvailabilityEntity.getSlotInterval();
        this.slotCapacity = serviceAvailabilityEntity.getSlotCapacity();
    }
}
