package com.aptmgt.customer.dto;

import com.aptmgt.commons.model.ServiceCategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCategoryResponseDTO {

    private Long serviceCategoryId;

    private String serviceCategoryName;

    public ServiceCategoryResponseDTO(ServiceCategoryEntity serviceCategoryEntity) {
        this.serviceCategoryId = serviceCategoryEntity.getServiceCategoryId();
        this.serviceCategoryName = serviceCategoryEntity.getServiceCategory();
    }

}
