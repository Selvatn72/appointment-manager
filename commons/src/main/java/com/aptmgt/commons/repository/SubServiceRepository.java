package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.ServiceEntity;
import com.aptmgt.commons.model.SubServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubServiceRepository extends JpaRepository<SubServiceEntity, Long> {
    List<SubServiceEntity> findByNameIgnoreCaseContaining(String name);

    List<SubServiceEntity> findByServiceId(ServiceEntity serviceEntity);
}
