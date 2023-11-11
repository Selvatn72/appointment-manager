package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.AvailableSlotsEntity;
import com.aptmgt.commons.model.ServiceEntity;
import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailableSlotsRepository extends JpaRepository<AvailableSlotsEntity,Long> {

    Optional<AvailableSlotsEntity> findByBranchIdAndServiceIdAndSlotDateAndStartTime(BranchEntity byBranchId, ServiceEntity byServiceId, Date date, Time startTime);

    List<AvailableSlotsEntity> findByServiceIdAndBranchIdAndSlotDate(ServiceEntity byServiceId, BranchEntity byBranchId, Date date);

    Optional<AvailableSlotsEntity> findByEmployeeIdAndServiceIdAndSlotDateAndStartTime(EmployeeEntity employeeEntity, ServiceEntity serviceEntity, Date date, Time startTime);

    List<AvailableSlotsEntity> findByServiceIdAndEmployeeIdAndSlotDate(ServiceEntity serviceById, EmployeeEntity employeeEntity, Date date);
}
