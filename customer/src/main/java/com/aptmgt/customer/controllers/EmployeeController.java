package com.aptmgt.customer.controllers;

import com.aptmgt.customer.dto.SearchBranchRequestDTO;
import com.aptmgt.customer.dto.TradingHoursResponseDTO;
import com.aptmgt.commons.dto.EmployeeResponseDTO;
import com.aptmgt.customer.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/get_city")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<String>> getCityDetails(@Valid @RequestBody SearchBranchRequestDTO searchBranchRequestDTO) {
        return employeeService.getCityDetails(searchBranchRequestDTO);
    }

    @PostMapping("/get_employee")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeeDetails(@Valid @RequestBody SearchBranchRequestDTO searchBranchRequestDTO) {
        return employeeService.getEmployeeDetails(searchBranchRequestDTO);
    }

    @GetMapping(params = "id")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<EmployeeResponseDTO> fetchEmployeeById(@RequestParam("id") Long employeeId) {
        return employeeService.fetchEmployeeById(employeeId);
    }

    @GetMapping(params = "employeeId")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<TradingHoursResponseDTO>> getServiceAvailabilityByEmployeeId(@RequestParam("employeeId") Long employeeId) {
        return employeeService.getServiceAvailabilityByEmployeeId(employeeId);
    }
}
