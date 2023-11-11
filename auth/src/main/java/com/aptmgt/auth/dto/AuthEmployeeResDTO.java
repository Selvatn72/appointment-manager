package com.aptmgt.auth.dto;

import com.aptmgt.commons.model.UserEntity;
import lombok.Data;

@Data
public class AuthEmployeeResDTO {

    private Long employeeId;

    private String accessToken;

    private Long serviceId;

    private String serviceName;

    private String type = "Bearer";

    private String role;

    private Integer roleId;

    private Long branchId;

    private Long serviceCategoryId;

    public AuthEmployeeResDTO(UserEntity user, String token) {
        this.employeeId = user.getEmployeeId().getEmployeeId();
        this.accessToken = token;
        this.serviceId = user.getEmployeeId().getServiceId() != null ? user.getEmployeeId().getServiceId().getServiceId() : null;
        this.serviceName = user.getEmployeeId().getServiceId() != null ? user.getEmployeeId().getServiceId().getServiceName() : null;
        this.role = user.getRoles().get(0).getName();
        this.roleId = user.getRoles().get(0).getRoleId();
        this.branchId = user.getEmployeeId().getBranchId().getBranchId();
        this.serviceCategoryId = user.getEmployeeId().getBranchId().getServiceCategoryId().getServiceCategoryId();
    }
}
