package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.UserEntity;
import com.aptmgt.commons.model.UserVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserVerificationRepo extends JpaRepository<UserVerificationEntity, Long> {

    Optional<UserVerificationEntity> findTop1ByUserIdOrderByIdDesc(UserEntity user);

    Optional<UserVerificationEntity> findTop1ByEmailOrderByIdDesc(String email);
}