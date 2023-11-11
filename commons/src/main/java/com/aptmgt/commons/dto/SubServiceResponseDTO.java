package com.aptmgt.commons.dto;

import com.aptmgt.commons.model.SubServiceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubServiceResponseDTO {

    private Long subServiceId;

    private String name;

    public SubServiceResponseDTO(SubServiceEntity subService) {
        this.subServiceId = subService.getSubServiceId();
        this.name = subService.getName();
    }

    public static List<SubServiceResponseDTO> getSubServices(List<SubServiceEntity> subServices) {
        List<SubServiceResponseDTO> serviceResponseDTOList = new ArrayList<>();
        subServices.forEach(subService -> serviceResponseDTOList.add(new SubServiceResponseDTO(subService)));
        return serviceResponseDTOList;
    }
}
