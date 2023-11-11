package com.aptmgt.admin.services;


import com.aptmgt.commons.dto.SubServiceResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ISubService {

    ResponseEntity<List<SubServiceResponseDTO>> getByServiceId(Long serviceId);
}
