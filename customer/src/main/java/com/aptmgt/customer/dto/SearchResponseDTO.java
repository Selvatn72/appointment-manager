package com.aptmgt.customer.dto;

import com.aptmgt.commons.dto.BranchResponseDTO;
import com.aptmgt.commons.dto.EmployeeResponseDTO;
import com.aptmgt.commons.dto.ServiceResponseDTO;
import com.aptmgt.commons.dto.SubServiceResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponseDTO {

    private List<BranchResponseDTO> branchResponse;
    private List<EmployeeResponseDTO> employeeResponse;
    private List<ServiceResponseDTO> serviceResponse;
    private List<SubServiceResponseDTO> subServiceResponse;

}
