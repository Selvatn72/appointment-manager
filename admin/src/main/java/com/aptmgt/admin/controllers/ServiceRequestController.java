package com.aptmgt.admin.controllers;

import com.aptmgt.admin.dto.ServiceRequestDTO;
import com.aptmgt.admin.dto.ServiceRequestResDTO;
import com.aptmgt.admin.services.IServiceRequestService;
import com.aptmgt.commons.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/service_request")
public class ServiceRequestController {

    @Autowired
    private IServiceRequestService iServiceRequestService;

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> saveServiceRequest(@Valid @RequestBody ServiceRequestDTO serviceRequestDTO) {
        return iServiceRequestService.saveServiceRequest(serviceRequestDTO);
    }

    @GetMapping("/{adminId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<List<ServiceRequestResDTO>> getServiceRequestList(@PathVariable Long adminId) {
        return iServiceRequestService.getServiceRequestList(adminId);
    }

}
