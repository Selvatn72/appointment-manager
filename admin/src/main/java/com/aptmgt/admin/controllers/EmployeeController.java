package com.aptmgt.admin.controllers;

import com.aptmgt.commons.dto.EmployeeInfoReqDTO;
import com.aptmgt.commons.dto.EmployeeResponseDTO;
import com.aptmgt.admin.services.IEmployeeService;
import com.aptmgt.commons.dto.MessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private IEmployeeService employeeService;

    @GetMapping("/{branchId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeeList(@PathVariable("branchId") Long branchId) {
        return employeeService.getEmployeeListByBranchId(branchId);
    }

    @DeleteMapping()
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MessageDTO> deleteEmployee(@RequestParam("id") Long employeeId) {
        return employeeService.deleteEmployee(employeeId);
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@RequestParam("id") Long employeeId) {
        return employeeService.getEmployeeById(employeeId);
    }

    @PutMapping(value = "/update")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<Object> registerEmployee(@RequestParam(value = "request") String request, @RequestParam(value = "image", required = false) MultipartFile file) throws AuthenticationException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        EmployeeInfoReqDTO employeeInfoReqDTO = objectMapper.readValue(request, EmployeeInfoReqDTO.class);
        return employeeService.updateEmployee(employeeInfoReqDTO, file);
    }

}
