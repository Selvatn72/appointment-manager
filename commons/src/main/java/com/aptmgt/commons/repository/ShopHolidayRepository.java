package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.ShopHolidayEntity;
import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopHolidayRepository extends JpaRepository<ShopHolidayEntity,Long> {

    List<ShopHolidayEntity> findByBranchId(BranchEntity branchEntity);

    Optional<ShopHolidayEntity> findByShopHolidayAndBranchId(String dateString, BranchEntity byBranchId);

    List<ShopHolidayEntity> findByEmployeeId(EmployeeEntity employeeEntity);

    Optional<ShopHolidayEntity> findByShopHolidayAndEmployeeId(String dateString, EmployeeEntity employeeEntity);
}
