package com.aptmgt.admin.dto;

import com.aptmgt.commons.model.ShopHolidayEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopHolidayResponseDTO {

    private Long shopHolidayId;

    private String shopHoliday;

    private String description;

    private Long employeeId;

    private Long branchId;

    public ShopHolidayResponseDTO(ShopHolidayEntity shopHolidayEntity) {
        this.shopHolidayId = shopHolidayEntity.getShopHolidayId();
        this.shopHoliday = shopHolidayEntity.getShopHoliday();
        this.description = shopHolidayEntity.getDescription();
        this.employeeId = shopHolidayEntity.getEmployeeId() != null ? shopHolidayEntity.getEmployeeId().getEmployeeId() : null;
        this.branchId = shopHolidayEntity.getBranchId().getBranchId();
    }
}