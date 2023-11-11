package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.EmployeeEntity;
import com.aptmgt.commons.model.PublicHolidayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PublicHolidayRepository extends JpaRepository<PublicHolidayEntity,Long> {

    List<PublicHolidayEntity> findByBranchId(BranchEntity branchEntity);

    Optional<PublicHolidayEntity> findByPublicHolidayAndBranchId(Date date, BranchEntity branchId);

    List<PublicHolidayEntity> findByEmployeeId(EmployeeEntity employeeEntity);

    Optional<PublicHolidayEntity> findByPublicHolidayAndEmployeeId(Date date, EmployeeEntity employeeEntity);
}
