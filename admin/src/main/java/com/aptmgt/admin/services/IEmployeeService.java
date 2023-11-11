package com.aptmgt.admin.services;

import com.aptmgt.commons.dto.EmployeeInfoReqDTO;
import com.aptmgt.commons.dto.EmployeeResponseDTO;
import com.aptmgt.commons.dto.MessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IEmployeeService {

    ResponseEntity<List<EmployeeResponseDTO>> getEmployeeListByBranchId(Long branchId);

    ResponseEntity<MessageDTO> deleteEmployee(Long employeeId);

    ResponseEntity<EmployeeResponseDTO> getEmployeeById(Long employeeId);

    ResponseEntity<Object> updateEmployee(EmployeeInfoReqDTO employeeInfoReqDTO, MultipartFile file);

}
