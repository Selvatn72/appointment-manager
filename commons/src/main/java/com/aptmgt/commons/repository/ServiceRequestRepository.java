package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.AdminEntity;
import com.aptmgt.commons.model.ServiceRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequestEntity, Long> {

    List<ServiceRequestEntity> findByAdminId(AdminEntity admin);
}
