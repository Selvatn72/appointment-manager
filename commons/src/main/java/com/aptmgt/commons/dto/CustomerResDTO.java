package com.aptmgt.commons.dto;

import com.aptmgt.commons.model.CustomerEntity;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResDTO {

    private Long customerId;

    private String name;

    private String email;

    private String phone;

    public CustomerResDTO(CustomerEntity customer) {
        this.customerId = customer.getCustomerId();
        this.name = customer.getName();
        this.email = customer.getEmail();
        this.phone = customer.getPhone();
    }
}
