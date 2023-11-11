package com.aptmgt.customer.dto;


import com.aptmgt.commons.model.TradingHoursEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradingHoursResponseDTO {

    private String startTime;

    private String endTime;

    private Boolean isWeekDay;

    private Boolean isWeekEnd;

    private Long branchId;

    private Long employeeId;

    public TradingHoursResponseDTO(TradingHoursEntity tradingHoursEntity){
      this.startTime = tradingHoursEntity.getStartTime();
      this.endTime = tradingHoursEntity.getEndTime();
      this.isWeekDay = tradingHoursEntity.getIsWeekDay();
      this.isWeekEnd = tradingHoursEntity.getIsWeekEnd();
      this.branchId = tradingHoursEntity.getBranchId().getBranchId();
      this.employeeId = tradingHoursEntity.getEmployeeId() != null ?tradingHoursEntity.getEmployeeId().getEmployeeId() : null;
    }
}
