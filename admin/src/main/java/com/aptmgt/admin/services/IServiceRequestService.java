package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.ServiceRequestDTO;
import com.aptmgt.admin.dto.ServiceRequestResDTO;
import com.aptmgt.commons.dto.MessageDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IServiceRequestService {

    ResponseEntity<MessageDTO> saveServiceRequest(ServiceRequestDTO serviceRequestDTO);

    ResponseEntity<List<ServiceRequestResDTO>> getServiceRequestList(Long adminId);
}
