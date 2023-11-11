package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.EmployeeEntity;
import com.aptmgt.commons.model.TradingHoursEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradingHoursRepository extends JpaRepository<TradingHoursEntity, Long> {

    List<TradingHoursEntity> findByBranchIdAndIsWeekDay(BranchEntity branchEntity, Boolean isWeekDay);

    List<TradingHoursEntity> findByBranchIdAndIsWeekEnd(BranchEntity branchEntity, Boolean isWeekEnd);

    List<TradingHoursEntity> findByEmployeeIdAndIsWeekDay(EmployeeEntity employeeEntity, Boolean bisWeekDay);

    List<TradingHoursEntity> findByEmployeeIdAndIsWeekEnd(EmployeeEntity employeeEntity, Boolean isWeekEnd);

    List<TradingHoursEntity> findByEmployeeId(EmployeeEntity employeeEntity);

    List<TradingHoursEntity> findByBranchId(BranchEntity branchEntity);
}
