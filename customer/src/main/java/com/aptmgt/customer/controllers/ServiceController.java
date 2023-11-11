package com.aptmgt.customer.controllers;

import com.aptmgt.customer.services.IServices;
import com.aptmgt.commons.dto.ServiceResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ServiceController {

    @Autowired
    private IServices iServices;

    @GetMapping("/get_services")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER')")
    public ResponseEntity<List<ServiceResponseDTO>> getBranchServices(@RequestParam Long serviceCategoryId) {
        return iServices.getBranchServices(serviceCategoryId);
    }

    @GetMapping("/service")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ServiceResponseDTO> getServiceById(@RequestParam Long serviceId) {
        return iServices.getServiceById(serviceId);
    }
}
