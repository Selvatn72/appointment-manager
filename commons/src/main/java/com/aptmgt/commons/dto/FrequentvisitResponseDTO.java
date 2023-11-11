package com.aptmgt.commons.dto;

import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.EmployeeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrequentvisitResponseDTO {

    private Long branchId;

    private Long employeeId;

    private Long serviceCategoryId;

    private String name;

    private String degree;

    private String branchName;

    private String city;
    public FrequentvisitResponseDTO(BranchEntity branchEntity) {
        this.branchId = branchEntity.getBranchId();
        this.name = branchEntity.getName();
        this.city = branchEntity.getCity();
        this.serviceCategoryId = branchEntity.getServiceCategoryId().getServiceCategoryId();
    }

    public FrequentvisitResponseDTO(EmployeeEntity employeeEntity) {
        this.branchId = employeeEntity.getBranchId().getBranchId();
        this.branchName = employeeEntity.getBranchId().getName();
        this.name = employeeEntity.getName();
        this.degree = employeeEntity.getDegree();
        this.employeeId = employeeEntity.getEmployeeId();
        this.city = employeeEntity.getCity();
        this.serviceCategoryId = employeeEntity.getServiceId().getServiceCategoryId().getServiceCategoryId();
    }
}
