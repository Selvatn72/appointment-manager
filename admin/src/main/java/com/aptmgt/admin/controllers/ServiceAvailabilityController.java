package com.aptmgt.admin.controllers;

import com.aptmgt.admin.dto.ServiceAvailabilityRequestDTO;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.dto.ServiceAvailabilityResponseDTO;
import com.aptmgt.admin.services.IServiceAvailability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/service_availability")
public class ServiceAvailabilityController {

    @Autowired
    private IServiceAvailability iServiceAvailability;

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> saveServiceAvailability(@Valid @RequestBody ServiceAvailabilityRequestDTO request) {
        return iServiceAvailability.saveServiceAvailability(request);
    }

    @GetMapping("/{branchId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<List<ServiceAvailabilityResponseDTO>> getServiceAvailabilityByBranchId(@PathVariable Long branchId) {
        return iServiceAvailability.getServiceAvailabilityByBranchId(branchId);
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<List<ServiceAvailabilityResponseDTO>> getServiceAvailabilityByEmployeeId(@RequestParam("employeeId") Long employeeId) {
        return iServiceAvailability.getServiceAvailabilityByEmployeeId(employeeId);
    }

    @DeleteMapping("/{serviceAvailabilityId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> deleteServiceAvailability(@PathVariable Long serviceAvailabilityId) {
        return iServiceAvailability.deleteServiceAvailability(serviceAvailabilityId);
    }
}
