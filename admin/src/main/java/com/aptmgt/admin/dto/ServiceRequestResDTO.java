package com.aptmgt.admin.dto;

import com.aptmgt.commons.model.ServiceRequestEntity;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequestResDTO {

    private Long serviceRequestId;

    private String serviceName;

    private Date createdAt;

    private Long serviceCategoryId;

    private String serviceCategoryName;

    private String status;

    public ServiceRequestResDTO(ServiceRequestEntity serviceRequestEntity) {
        this.serviceRequestId = serviceRequestEntity.getServiceRequestId();
        this.serviceName = serviceRequestEntity.getServiceName();
        this.createdAt = serviceRequestEntity.getCreatedDate();
        this.status = serviceRequestEntity.getStatus();
        this.serviceCategoryId = serviceRequestEntity.getServiceCategoryId().getServiceCategoryId();
        this.serviceCategoryName = serviceRequestEntity.getServiceCategoryId().getServiceCategory();
    }
}
