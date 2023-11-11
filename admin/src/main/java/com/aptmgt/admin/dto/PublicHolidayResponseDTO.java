package com.aptmgt.admin.dto;

import com.aptmgt.commons.model.PublicHolidayEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicHolidayResponseDTO {

    private Long publicHolidayId;

    private Date publicHoliday;

    private String description;

    private Long employeeId;

    private Long branchId;

    public PublicHolidayResponseDTO(PublicHolidayEntity publicHolidayEntity) {
        this.publicHolidayId = publicHolidayEntity.getPublicHolidayId();
        this.publicHoliday = publicHolidayEntity.getPublicHoliday();
        this.description = publicHolidayEntity.getDescription();
        this.employeeId = publicHolidayEntity.getEmployeeId() != null ? publicHolidayEntity.getEmployeeId().getEmployeeId() : null;
        this.branchId = publicHolidayEntity.getBranchId().getBranchId();
    }
}
