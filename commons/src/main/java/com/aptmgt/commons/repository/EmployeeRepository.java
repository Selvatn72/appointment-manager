package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.EmployeeEntity;
import com.aptmgt.commons.model.ServiceEntity;
import com.aptmgt.commons.model.SubServiceEntity;
import com.aptmgt.commons.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {

    List<EmployeeEntity> findByBranchIdAndIsActive(BranchEntity branchEntity, boolean isActive);

    List<EmployeeEntity> findByCityAndBranchIdAndIsActive(String city, BranchEntity branchById, Boolean isActive);

    List<EmployeeEntity> findByCityAndEmployeeIdAndIsActive(String city, Long employeeById, Boolean isActive);

    List<EmployeeEntity> findByCityAndServiceIdAndIsActive(String city, ServiceEntity serviceById, Boolean isActive);

    List<EmployeeEntity> findByCityAndSubServicesAndIsActive(String city, SubServiceEntity subServiceById, Boolean isActive);

    @Query("select distinct b.city from BranchEntity b where b.city like concat('%' ,:city, '%')")
    List<String> findByCityStartsWith(String city);

    List<EmployeeEntity> findByIsActiveAndNameIgnoreCaseContainingAndBranchIdIn(Boolean isActive, String name, List<BranchEntity> branchEntity);
    List<EmployeeEntity> findByCityAndIsActive(String city, Boolean isActive);

    List<EmployeeEntity> findByIsActiveAndCityAndBranchIdInAndNameIgnoreCaseContaining(Boolean isActive, String city,List<BranchEntity> branchEntityList, String name);

    List<EmployeeEntity> findByIsActiveAndCityIgnoreCaseContainingAndServiceIdNotNull(Boolean isActive ,String city);

    @Query("select distinct b.city from BranchEntity b")
    List<String> findByCity();

    Optional<EmployeeEntity> findByIsActiveAndBranchId(boolean isActive, BranchEntity branchEntity);

    List<EmployeeEntity> findByIsActiveAndBranchIdIn(Boolean isActive, List<BranchEntity> branchEntityList);
}
