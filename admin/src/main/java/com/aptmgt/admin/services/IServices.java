package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.ServiceRequestDTO;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.dto.ServiceResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IServices {

    ResponseEntity<MessageDTO> saveService(ServiceRequestDTO service);

    ResponseEntity<MessageDTO> deleteService(Long serviceId);

    ResponseEntity<List<ServiceResponseDTO>> fetchByServiceCategoryId(Long serviceCategoryId);
}
