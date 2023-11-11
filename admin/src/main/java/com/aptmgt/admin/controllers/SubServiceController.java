package com.aptmgt.admin.controllers;

import com.aptmgt.admin.services.ISubService;
import com.aptmgt.commons.dto.SubServiceResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/sub_service")
public class SubServiceController {

    @Autowired
    private ISubService iSubService;

    @GetMapping("/{serviceId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<List<SubServiceResponseDTO>> getByServiceId(@PathVariable Long serviceId) {
        return iSubService.getByServiceId(serviceId);
    }

}
