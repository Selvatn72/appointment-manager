package com.aptmgt.customer.services;

import com.aptmgt.commons.dto.ServiceResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IServices {

    ResponseEntity<List<ServiceResponseDTO>> getBranchServices(Long serviceCategoryId);

    ResponseEntity<ServiceResponseDTO> getServiceById(Long serviceId);
}
