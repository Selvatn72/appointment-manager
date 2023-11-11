package com.aptmgt.admin.dto;

import com.aptmgt.commons.model.TradingHoursEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeekdayHoursResponseDTO {

    private Long weekdayHoursId;

    private String startTime;

    private String endTime;

    private Long employeeId;

    private Long branchId;

    public WeekdayHoursResponseDTO(TradingHoursEntity tradingHoursEntity) {
        this.weekdayHoursId = tradingHoursEntity.getTradingHoursId();
        this.startTime = tradingHoursEntity.getStartTime();
        this.endTime = tradingHoursEntity.getEndTime();
        this.employeeId = tradingHoursEntity.getEmployeeId() != null ? tradingHoursEntity.getEmployeeId().getEmployeeId() : null;
        this.branchId = tradingHoursEntity.getBranchId().getBranchId();
    }
}