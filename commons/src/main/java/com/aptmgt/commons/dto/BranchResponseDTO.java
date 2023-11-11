package com.aptmgt.commons.dto;

import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.BranchFilesEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchResponseDTO {

    private Long branchId;

    private String name;

    private String phone;

    private String mail;

    private String address;

    private String website;

    private String city;

    private String stateName;

    private String countryName;

    private String zipcode;

    private Long adminId;

    private Long serviceCategoryId;

    private List<ServiceResponseDTO> services;

    private Float latitude;

    private Float longitude;

    private byte[] image;

    private String fileName;

    private Boolean isPhotoVerified;

    private List<BranchFilesResponseDTO> files;

    public BranchResponseDTO(BranchEntity branchEntity, List<BranchFilesEntity> files) {
        this.branchId = branchEntity.getBranchId();
        this.name = branchEntity.getName();
        this.phone = branchEntity.getPhone();
        this.mail = branchEntity.getMail();
        this.address = branchEntity.getAddress();
        this.website = branchEntity.getWebsite();
        this.city = branchEntity.getCity();
        this.stateName = branchEntity.getState();
        this.countryName = branchEntity.getCountry();
        this.zipcode = branchEntity.getZipcode();
        this.adminId = branchEntity.getAdminId().getAdminId();
        this.serviceCategoryId = branchEntity.getServiceCategoryId().getServiceCategoryId();
        this.services = !branchEntity.getServices().isEmpty() ? ServiceResponseDTO.getServices(branchEntity.getServices()) : null;
        this.latitude = branchEntity.getLatitude();
        this.longitude = branchEntity.getLongitude();
        this.image = branchEntity.getImage();
        this.fileName = branchEntity.getFileName();
        this.isPhotoVerified = branchEntity.getIsPhotoVerified();
        this.files = (files != null && !files.isEmpty()) ? BranchFilesResponseDTO.getBranchFiles(files) : null;
    }

    public static List<BranchResponseDTO> getBranch(List<BranchEntity> branchEntities) {
        List<BranchResponseDTO> branchResponseDTOList = new ArrayList<>();
        branchEntities.forEach(branch -> branchResponseDTOList.add(new BranchResponseDTO(branch, null)));
        return branchResponseDTOList;
    }
}
