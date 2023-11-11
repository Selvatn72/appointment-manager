package com.aptmgt.customer.dto;

import com.aptmgt.commons.dto.BranchResponseDTO;
import com.aptmgt.commons.dto.EmployeeResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverAllSearchResponseDTO {

        private List<BranchResponseDTO> branchResponse;
        private List<EmployeeResponseDTO> employeeResponse;
}
