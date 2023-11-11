package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.ServiceCategoryEntity;
import com.aptmgt.commons.model.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    List<ServiceEntity> findByServiceCategoryId(ServiceCategoryEntity serviceCategoryEntity);

    List<ServiceEntity> findByServiceCategoryIdAndServiceNameIgnoreCaseContaining(ServiceCategoryEntity ServiceCategoryId, String name);

    List<ServiceEntity> findByAndServiceCategoryIdAndServiceNameIgnoreCaseContaining(ServiceCategoryEntity serviceCategory, String name);

    List<ServiceEntity> findByServiceNameIgnoreCaseContaining(String name);
}
