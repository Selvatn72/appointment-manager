package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.AdminEntity;
import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.ServiceCategoryEntity;
import com.aptmgt.commons.model.ServiceEntity;
import com.aptmgt.commons.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity,Long> {

    List<BranchEntity> findByAdminIdAndIsActive(AdminEntity admin, Boolean isActive);
    @Query("select distinct b.city from BranchEntity b where b.city like concat(:city, '%')")
    List<String> findByCityStartsWith(String city);

    List<BranchEntity> findByIsActiveAndCityOrderByBranchIdDesc(Boolean isActive, String name);

    List<BranchEntity> findByIsActiveAndCityAndServiceCategoryIdAndNameIgnoreCaseContaining(Boolean isActive, String city, ServiceCategoryEntity ServiceCategoryId, String name);


    List<BranchEntity> findByIsActiveAndCityAndNameIgnoreCaseContaining(Boolean isActive, String city, String name);

    List<BranchEntity> findByIsActiveAndCityAndServices(Boolean isActive, String city, ServiceEntity serviceById);

    List<BranchEntity> findByIsActiveAndServiceCategoryIdAndServicesAndCityIgnoreCaseContaining(Boolean isActive, ServiceCategoryEntity byServiceCategoryId, ServiceEntity serviceById, String name);

    List<BranchEntity> findByIsActiveAndServiceCategoryIdAndCityIgnoreCaseContaining(Boolean isActive, ServiceCategoryEntity byServiceCategoryId, String name);

    List<BranchEntity> findByIsActiveAndCityIgnoreCaseContainingAndServiceCategoryIdNot(Boolean isActive,String city, ServiceCategoryEntity serviceCategory);

    List<BranchEntity> findByIsActiveAndCityIgnoreCaseContaining(Boolean b, String city);

    List<BranchEntity> findByIsActiveAndBranchIdAndCityIgnoreCaseContaining(Boolean isActive, Long branchId, String name);

    List<BranchEntity> findByIsActiveAndBranchId(Boolean isActive, Long branchId);


    @Query("select distinct b.city from BranchEntity b where b.latitude like concat(:latitude, '%') and b.longitude like concat(:longitude, '%')  ")
    List<String> findByLatitudeStartsWithAndLongitudeStartsWith(Long latitude, Long longitude);

    List<BranchEntity> findByIsActiveAndCityAndServiceCategoryId(Boolean isActive, String city, ServiceCategoryEntity byServiceCategoryId);
}
