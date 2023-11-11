package com.aptmgt.commons.dto;

import com.aptmgt.commons.model.BranchFilesEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchFilesResponseDTO {

    private Long branchFileId;

    private byte[] image;

    private Boolean isPhotoVerified;

    private Long branchId;

    private String branchName;

    private Long serviceCategoryId;

    public BranchFilesResponseDTO(BranchFilesEntity branchFilesEntity) {
        this.branchFileId = branchFilesEntity.getBranchFilesId();
        this.image = branchFilesEntity.getImage();
        this.isPhotoVerified = branchFilesEntity.getIsPhotoVerified();
        this.branchId = branchFilesEntity.getBranchId().getBranchId();
        this.branchName = branchFilesEntity.getBranchId().getName();
        this.serviceCategoryId = branchFilesEntity.getBranchId().getServiceCategoryId().getServiceCategoryId();
    }

    public static List<BranchFilesResponseDTO> getBranchFiles(List<BranchFilesEntity> files) {
        List<BranchFilesResponseDTO> branchFilesResponseDTOList = new ArrayList<>();
        files.forEach(file -> {
            branchFilesResponseDTOList.add(new BranchFilesResponseDTO(file));
        });
        return branchFilesResponseDTOList;
    }
}
