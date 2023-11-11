package com.aptmgt.auth.dto;

import com.aptmgt.commons.model.UserEntity;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthAdminResDTO {

    private Long adminId;

    private String accessToken;

    private Long serviceCategoryId;

    private String shopName;

    private String type = "Bearer";

    private String role;

    private Integer roleId;

    public AuthAdminResDTO(UserEntity user, String token) {
        this.adminId = user.getAdminId().getAdminId();
        this.accessToken = token;
        this.serviceCategoryId = user.getAdminId().getServiceCategoryId().getServiceCategoryId();
        this.shopName = user.getAdminId().getShopName();
        this.role = user.getRoles().get(0).getName();
        this.roleId = user.getRoles().get(0).getRoleId();
    }
}
