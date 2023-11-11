package com.aptmgt.auth.dto;

import com.aptmgt.commons.model.AdminEntity;
import com.aptmgt.commons.model.CustomerEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsResDTO {

    private Long id;

    private String name;

    private String shopName;

    private String email;

    private String phone;

    public UserDetailsResDTO(AdminEntity adminEntity) {
        this.id = adminEntity.getAdminId();
        this.name = adminEntity.getName();
        this.email = adminEntity.getEmail();
        this.shopName = adminEntity.getShopName();
        this.phone = adminEntity.getPhone();
    }

    public UserDetailsResDTO(CustomerEntity customerEntity) {
        this.id = customerEntity.getCustomerId();
        this.name = customerEntity.getName();
        this.email = customerEntity.getEmail();
        this.phone = customerEntity.getPhone();
    }
}
