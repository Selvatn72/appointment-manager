package com.aptmgt.commons.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FrequentDTO {

    private Long employeeId;
    private Long branchId;
    private Long count;

    public FrequentDTO(Long employeeId,  Long count){
        this.employeeId = employeeId;
        this.count = count;
    }

    public FrequentDTO(Long employeeId, Long branchId, Long count){
        this.employeeId = employeeId;
        this.branchId = branchId;
        this.count = count;
    }


}
