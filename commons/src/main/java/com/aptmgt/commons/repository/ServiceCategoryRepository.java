package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.ServiceCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCategoryRepository extends JpaRepository<ServiceCategoryEntity, Long> {
}
