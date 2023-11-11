package com.aptmgt.commons.dto;

import com.aptmgt.commons.model.EmployeeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {

    private Long employeeId;

    private Long branchId;

    private String name;

    private String email;

    private String phone;

    private String branchPhone;
    private String city;

    private String degree;

    private Integer experience;

    private Long serviceCategoryId;

    private Long serviceId;

    private String serviceName;

    private String branchName;

    private String address;

    private Float latitude;

    private Float longitude;

    private byte[] image;

    private String fileName;

    private List<SubServiceResponseDTO> subServices;

    public EmployeeResponseDTO(EmployeeEntity employeeEntity) {
        this.employeeId = employeeEntity.getEmployeeId();
        this.name = employeeEntity.getName();
        this.email = employeeEntity.getEmail();
        this.city = employeeEntity.getCity();
        this.degree = employeeEntity.getDegree();
        this.experience = employeeEntity.getExperience();
        this.serviceCategoryId = employeeEntity.getBranchId().getServiceCategoryId().getServiceCategoryId();
        this.serviceId = employeeEntity.getServiceId() != null ? employeeEntity.getServiceId().getServiceId() : null;
        this.branchPhone = employeeEntity.getBranchId().getPhone();
        this.phone = employeeEntity.getPhone();
        this.serviceName = employeeEntity.getServiceId() != null ? employeeEntity.getServiceId().getServiceName() : null;
        this.branchName = employeeEntity.getBranchId().getName();
        this.address = employeeEntity.getBranchId().getAddress();
        this.latitude = employeeEntity.getBranchId().getLatitude();
        this.longitude = employeeEntity.getBranchId().getLongitude();
        this.branchId = employeeEntity.getBranchId().getBranchId();
        this.image = employeeEntity.getImage();
        this.fileName = employeeEntity.getFileName();
        this.subServices = !employeeEntity.getSubServices().isEmpty() ? SubServiceResponseDTO.getSubServices(employeeEntity.getSubServices()) : null;
    }

    public static List<EmployeeResponseDTO> getEmployee(List<EmployeeEntity> employeeEntityList) {
        List<EmployeeResponseDTO> employeeResponseDTOList = new ArrayList<>();
        employeeEntityList.forEach(employee -> employeeResponseDTOList.add(new EmployeeResponseDTO(employee)));
        return employeeResponseDTOList;
    }
}
