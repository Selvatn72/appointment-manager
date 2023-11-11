package com.aptmgt.customer.services;

import com.aptmgt.customer.dto.SearchBranchRequestDTO;
import com.aptmgt.customer.dto.TradingHoursResponseDTO;
import com.aptmgt.commons.dto.EmployeeResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmployeeService {


    ResponseEntity<List<String>> getCityDetails(SearchBranchRequestDTO searchBranchRequestDTO);

    ResponseEntity<List<EmployeeResponseDTO>> getEmployeeDetails(SearchBranchRequestDTO searchBranchRequestDTO);

    ResponseEntity<EmployeeResponseDTO> fetchEmployeeById(Long employeeId);

    ResponseEntity<List<TradingHoursResponseDTO>> getServiceAvailabilityByEmployeeId(Long employeeId);
}
