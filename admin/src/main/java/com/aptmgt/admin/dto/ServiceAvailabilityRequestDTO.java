package com.aptmgt.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAvailabilityRequestDTO {

    @NotNull(message = "Branch id field should be not null")
    private Long branchId;

    private Long employeeId;

    @NotNull(message = "Slot details field should be not null")
    private List<SlotDetailsDTO> slotDetails;
}
