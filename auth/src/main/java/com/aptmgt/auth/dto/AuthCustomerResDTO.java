package com.aptmgt.auth.dto;

import com.aptmgt.commons.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthCustomerResDTO {

    private Long customerId;

    private String customerName;

    private String accessToken;

    private String type = "Bearer";

    private String role;

    private Integer roleId;

    private Timestamp createdDate;

    public AuthCustomerResDTO(UserEntity user, String token) {
        this.customerId = user.getCustomerId().getCustomerId();
        this.customerName = user.getCustomerId().getName();
        this.accessToken = token;
        this.role = user.getRoles().get(0).getName();
        this.roleId = user.getRoles().get(0).getRoleId();
        this.createdDate = user.getCustomerId().getCreatedDate();
    }
}
