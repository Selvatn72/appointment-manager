package com.aptmgt.admin.controllers;

import com.aptmgt.admin.dto.ServiceRequestDTO;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.dto.ServiceResponseDTO;
import com.aptmgt.admin.services.IServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/service")
public class ServiceController {

    @Autowired
    private IServices iServices;

    @PostMapping()
    public ResponseEntity<MessageDTO> saveService(@RequestBody ServiceRequestDTO serviceRequestDTO) {
        return iServices.saveService(serviceRequestDTO);
    }

    @GetMapping("/{serviceCategoryId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<List<ServiceResponseDTO>> fetchByServiceCategoryId(@PathVariable Long serviceCategoryId) {
        return iServices.fetchByServiceCategoryId(serviceCategoryId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDTO> deleteService(@PathVariable("id") Long serviceId) {
        return iServices.deleteService(serviceId);
    }
}
