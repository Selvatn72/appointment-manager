package com.aptmgt.commons.repository;


import com.aptmgt.commons.model.AppointmentStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentStatusRepository extends JpaRepository<AppointmentStatusEntity, Integer> {

}
