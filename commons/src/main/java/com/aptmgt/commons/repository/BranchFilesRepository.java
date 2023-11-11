package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.BranchFilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchFilesRepository extends JpaRepository<BranchFilesEntity, Long> {

    List<BranchFilesEntity> findByBranchId(BranchEntity branchEntity);

    List<BranchFilesEntity> findByIsPhotoVerifiedAndBranchIdIn(Boolean isPhotoVerified, List<BranchEntity> branchEntity);
}
