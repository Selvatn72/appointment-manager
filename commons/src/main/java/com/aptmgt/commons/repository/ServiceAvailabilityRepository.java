package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.ServiceEntity;
import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.EmployeeEntity;
import com.aptmgt.commons.model.ServiceAvailabilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceAvailabilityRepository extends JpaRepository<ServiceAvailabilityEntity, Long> {

    List<ServiceAvailabilityEntity> findByBranchId(BranchEntity branchId);

    Optional<ServiceAvailabilityEntity> findByBranchIdAndServiceIdAndSlotDay(BranchEntity branchEntity, ServiceEntity serviceEntity, String dateString);

    List<ServiceAvailabilityEntity> findByEmployeeId(EmployeeEntity employeeEntity);

    Optional<ServiceAvailabilityEntity> findByEmployeeIdAndServiceIdAndSlotDay(EmployeeEntity employeeEntity, ServiceEntity serviceById, String dateString);

    List<ServiceAvailabilityEntity> findByBranchIdAndSlotDayIn(BranchEntity branchId, List<String> weekDays);
}
