package com.aptmgt.customer.controllers;

import com.aptmgt.customer.dto.ServiceCategoryResponseDTO;
import com.aptmgt.customer.services.IServiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ServiceCategoryController {

    @Autowired
    private IServiceCategoryService iServiceCategoryService;

    @GetMapping("/get_categories")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<ServiceCategoryResponseDTO>> getBranchCategory() {
        return iServiceCategoryService.getBranchCategory();
    }

}
