package com.aptmgt.commons.repository;

import com.aptmgt.commons.model.*;
import com.aptmgt.commons.dto.FrequentDTO;
import com.aptmgt.commons.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findByBranchId(BranchEntity branchEntity);

    List<AppointmentEntity> findByCustomerIdAndAppointmentDateOrderByStartTimeAsc(CustomerEntity customerId, Date date);

    List<AppointmentEntity> findByCustomerIdAndAndAppointmentDateAfterOrderByAppointmentDateAscStartTimeAsc(CustomerEntity customerId, Date date);

    List<AppointmentEntity> findByCustomerIdAndAppointmentDateBeforeOrderByAppointmentDateDescStartTimeAsc(CustomerEntity customerId, Date date);

    List<AppointmentEntity> findByCustomerIdAndAppointmentDateAndAppointmentStatusIdAndStartTimeAfterOrderByStartTimeDesc(CustomerEntity customerById, Date date, AppointmentStatusEntity appointmentStatusById, Time startTime);

    List<AppointmentEntity> findByBranchIdAndAppointmentDate(BranchEntity branchId, Date date);

    List<AppointmentEntity> findByBranchIdAndAppointmentDateBetween(BranchEntity branchEntity, Date startDate, Date endDate);

    @Query(value = "SELECT NEW com.aptmgt.commons.dto.FrequentDTO(u.employeeId.employeeId,u.branchId.branchId, count(u.employeeId.employeeId)) FROM AppointmentEntity u WHERE u.customerId.customerId = :customerId AND u.employeeId.employeeId IS NOT NULL GROUP BY u.employeeId.employeeId")
    List<FrequentDTO> findEmployeeFrequentVisitList(Long customerId);

    @Query(value = "SELECT NEW com.aptmgt.commons.dto.FrequentDTO(u.employeeId.employeeId, u.branchId.branchId, count(u.branchId.branchId)) FROM AppointmentEntity u WHERE u.customerId.customerId = :customerId AND u.employeeId.employeeId IS NULL GROUP BY u.branchId.branchId")
    List<FrequentDTO> findBranchFrequentVisitList(Long customerId);

    List<AppointmentEntity> findByBranchIdAndEmployeeIdAndAppointmentDate(BranchEntity branchById, EmployeeEntity employeeEntity, Date publicHoliday);

    List<AppointmentEntity> findByEmployeeId(EmployeeEntity employeeEntity);

    @Query("select a from AppointmentEntity a where a.employeeId.employeeId = :employeeId and a.customerId.customerId = :customerId and a.appointmentStatusId.appointmentStatusId = :appointmentStatusId and ((a.appointmentDate > current_date()) or (a.appointmentDate = current_date() and a.startTime >= current_time()))")
    List<AppointmentEntity> findActiveAppointmentsForEmployee(Long employeeId, Long customerId, Integer appointmentStatusId);

    @Query("select a from AppointmentEntity a where a.branchId.branchId = :branchId and a.serviceId.serviceId = :serviceId and a.customerId.customerId = :customerId and a.appointmentStatusId.appointmentStatusId = :appointmentStatusId and ((a.appointmentDate > current_date()) or (a.appointmentDate = current_date() and a.startTime >= current_time()))")
    List<AppointmentEntity> findActiveAppointmentsForBranch(Long branchId,Long serviceId, Long customerId, Integer appointmentStatusId);

    //Optional<AppointmentEntity> findByEmployeeIdAndCustomerIdAndAppointmentStatusIdAndAppointmentDateAndStartTimeAfter(EmployeeEntity employeeById, CustomerEntity customerById, AppointmentStatusEntity appointmentStatusById, Date currentDate);

    @Query("select a from AppointmentEntity a where a.appointmentStatusId.appointmentStatusId = 1 and ((a.appointmentDate < current_date()) or (a.appointmentDate = current_date() and a.endTime < ADDTIME(current_time(), '05:30:00')))")
    List<AppointmentEntity> findAllAbandonedAppointments();

    Page<AppointmentEntity> findByBranchIdAndCustomerId(BranchEntity branchEntity, CustomerEntity customerEntity, Pageable paging);

    Page<AppointmentEntity> findByBranchIdAndAppointmentDateBetween(BranchEntity branchEntity, Date startDate, Date endDate, Pageable paging);

    Page<AppointmentEntity> findByBranchIdAndAppointmentDateBetweenAndAppointmentStatusId(BranchEntity branchEntity, Date startDate, Date endDate, AppointmentStatusEntity appointmentStatus, Pageable paging);

    Page<AppointmentEntity> findByBranchIdAndAppointmentDateAndAppointmentStatusId(BranchEntity branchEntity, Date date, AppointmentStatusEntity appointmentStatus, Pageable paging);

    Page<AppointmentEntity> findByBranchIdAndAppointmentDate(BranchEntity branchId, Date date, Pageable paging);

    Page<AppointmentEntity> findByEmployeeIdAndCustomerId(EmployeeEntity employeeEntity, CustomerEntity customerEntity, Pageable paging);

    Page<AppointmentEntity> findByEmployeeIdAndAppointmentDateBetweenAndAppointmentStatusId(EmployeeEntity employeeEntity, Date startDate, Date endDate, AppointmentStatusEntity appointmentStatus, Pageable paging);

    Page<AppointmentEntity> findByEmployeeIdAndAppointmentDateBetween(EmployeeEntity employeeEntity, Date startDate, Date endDate, Pageable paging);

    Page<AppointmentEntity> findByEmployeeIdAndAppointmentDateAndAppointmentStatusId(EmployeeEntity employeeEntity, Date date, AppointmentStatusEntity appointmentStatus, Pageable paging);

    Page<AppointmentEntity> findByEmployeeIdAndAppointmentDate(EmployeeEntity employeeEntity, Date date, Pageable paging);

    Optional<AppointmentEntity> findByAppointmentStatusIdAndAppointmentDateAndStartTime(AppointmentStatusEntity appointmentStatusById, Date date, Time time);
}
