package com.aptmgt.admin.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchServicesDTO {

    @NotNull(message = "Branch id field should be not null")
    private Long branchId;

    private List<Long> servicesId;
}
