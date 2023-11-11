package com.aptmgt.commons.dto;

import com.aptmgt.commons.model.ServiceEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDTO {

    private Long serviceId;

    private String serviceName;

    public ServiceResponseDTO(ServiceEntity service) {
        this.serviceId = service.getServiceId();
        this.serviceName = service.getServiceName();
    }

    public static List<ServiceResponseDTO> getServices(Set<ServiceEntity> services) {
        List<ServiceResponseDTO> serviceResponseDTOList = new ArrayList<>();
        services.forEach(service -> serviceResponseDTOList.add(new ServiceResponseDTO(service)));
        return serviceResponseDTOList;
    }
}
