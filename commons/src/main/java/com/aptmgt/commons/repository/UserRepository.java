package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.CustomerEntity;
import com.aptmgt.commons.model.AdminEntity;
import com.aptmgt.commons.model.EmployeeEntity;
import com.aptmgt.commons.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUsername(String username);

    UserEntity findByEmail(String email);

    Optional<UserEntity> findByEmailAndAdminIdIsNotNull(String email);

    Optional<UserEntity> findByEmailAndCustomerIdIsNotNull(String email);

    Optional<UserEntity> findByAdminId(AdminEntity admin);

    Optional<UserEntity> findByCustomerId(CustomerEntity customer);

    Optional<UserEntity> findByEmailAndEmployeeIdIsNotNull(String email);

    Optional<UserEntity> findByEmployeeId(EmployeeEntity employee);

    Optional<UserEntity> findByEmailAndIsActiveAndEmployeeIdIsNotNull(String email, Boolean isActive);

    Optional<UserEntity> findByEmailAndIsActiveAndAdminIdIsNotNull(String email, Boolean isActive);

    Optional<UserEntity> findByEmailAndIsActiveAndCustomerIdIsNotNull(String email, Boolean isActive);

    Optional<UserEntity> findByPhoneAndIsActiveAndCustomerIdIsNotNull(String phone, Boolean isActive);
}
