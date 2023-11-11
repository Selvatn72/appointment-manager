package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.PublicHolidayRequestDTO;
import com.aptmgt.admin.dto.PublicHolidayResponseDTO;
import com.aptmgt.admin.services.email.EmailService;
import com.aptmgt.commons.model.*;
import com.aptmgt.commons.repository.*;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.utils.Constants;
import com.aptmgt.commons.utils.ResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PublicHolidayServiceImpl implements IPublicHolidayService {

    @Autowired
    private PublicHolidayRepository publicHolidayRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AvailableSlotsRepository availableSlotsRepository;

    @Autowired
    private AppointmentStatusRepository appointmentStatusRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmailService emailService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicHolidayServiceImpl.class);

    @Override
    public ResponseEntity<MessageDTO> savePublicHoliday(PublicHolidayRequestDTO publicHoliday) {
        PublicHolidayEntity publicHolidayEntity = new PublicHolidayEntity();
        publicHolidayEntity.setBranchId(findBranchById(publicHoliday.getBranchId()));
        publicHolidayEntity.setEmployeeId(publicHoliday.getEmployeeId() != null ? findEmployeeById(publicHoliday.getEmployeeId()) : null);
        try {
            publicHolidayEntity.setPublicHoliday(publicHoliday.getPublicHoliday());
            publicHolidayEntity.setDescription(publicHoliday.getDescription());
            publicHolidayEntity.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            publicHolidayEntity.setCreatedDate(new Timestamp(new Date().getTime()));
            publicHolidayRepository.save(publicHolidayEntity);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.PUBLIC_HOLIDAY_CREATED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private EmployeeEntity findEmployeeById(Long employeeId) {
        Optional<EmployeeEntity> employeeEntityOptional = employeeRepository.findById(employeeId);
        if (!employeeEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.EMPLOYEE_NOT_FOUND);
        }
        return employeeEntityOptional.get();
    }

    public PublicHolidayEntity findPublicHolidayById(Long publicHolidayId) {
        Optional<PublicHolidayEntity> publicHolidayEntityOptional = publicHolidayRepository.findById(publicHolidayId);
        if (!publicHolidayEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.PUBLIC_HOLIDAY_NOT_FOUND);
        }
        return publicHolidayEntityOptional.get();
    }

    @Override
    public ResponseEntity<PublicHolidayResponseDTO> fetchByPublicHolidayId(Long id) {
        return ResponseEntity.ok(new PublicHolidayResponseDTO(findPublicHolidayById(id)));
    }

    @Override
    public ResponseEntity<MessageDTO> updatePublicHoliday(Long id, PublicHolidayRequestDTO publicHoliday) {
        PublicHolidayEntity publicHolidayEntity = findPublicHolidayById(id);
        publicHolidayEntity.setBranchId(findBranchById(publicHoliday.getBranchId()));
        publicHolidayEntity.setEmployeeId(publicHoliday.getEmployeeId() != null ? findEmployeeById(publicHoliday.getEmployeeId()) : null);
        try {
            publicHolidayEntity.setPublicHoliday(publicHoliday.getPublicHoliday());
            publicHolidayEntity.setDescription(publicHoliday.getDescription());
            publicHolidayEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            publicHolidayEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            publicHolidayRepository.save(publicHolidayEntity);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.PUBLIC_HOLIDAY_UPDATED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private BranchEntity findBranchById(Long branchId) {
        Optional<BranchEntity> branchEntityOptional = branchRepository.findById(branchId);
        if (!branchEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.BRANCH_NOT_FOUND);
        }
        return branchEntityOptional.get();
    }

    @Override
    public ResponseEntity<MessageDTO> deletePublicHoliday(Long id) {
        PublicHolidayEntity publicHolidayEntity = findPublicHolidayById(id);
        try {
            publicHolidayEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            publicHolidayEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            publicHolidayRepository.save(publicHolidayEntity);

            publicHolidayRepository.deleteById(id);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.PUBLIC_HOLIDAY_DELETED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<List<PublicHolidayResponseDTO>> fetchPublicHolidaysByBranchId(Long branchId) {
        BranchEntity branchEntity = findBranchById(branchId);
        try {
            List<PublicHolidayResponseDTO> publicHolidayResponseDTOList = new ArrayList<>();
            List<PublicHolidayEntity> publicHolidayEntityList = publicHolidayRepository.findByBranchId(branchEntity);
            if (!publicHolidayEntityList.isEmpty()) {
                publicHolidayEntityList.forEach(publicHoliday -> publicHolidayResponseDTOList.add(new PublicHolidayResponseDTO(publicHoliday)));
            }
            return ResponseEntity.ok(publicHolidayResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Async
    @Override
    public void updateAppointments(PublicHolidayRequestDTO publicHoliday, Authentication authentication) {
        List<AppointmentEntity> appointmentEntityList;
        if (publicHoliday.getEmployeeId() != null) {
            appointmentEntityList = appointmentRepository.findByBranchIdAndEmployeeIdAndAppointmentDate(findBranchById(publicHoliday.getBranchId()), findEmployeeById(publicHoliday.getEmployeeId()), publicHoliday.getPublicHoliday());
        } else {
            appointmentEntityList = appointmentRepository.findByBranchIdAndAppointmentDate(findBranchById(publicHoliday.getBranchId()), publicHoliday.getPublicHoliday());
        }

        try {
            String username = authentication.getName();
            if (!appointmentEntityList.isEmpty()) {
                appointmentEntityList.forEach(appointmentEntity -> {
                    if (appointmentEntity.getAppointmentStatusId().getAppointmentStatusId() != 4 && appointmentEntity.getAppointmentStatusId().getAppointmentStatusId() != 5) {
                        appointmentEntity.setAppointmentStatusId(findAppointmentStatusById(5));
                        appointmentEntity.setReasonForCancel(publicHoliday.getDescription());
                        appointmentEntity.setUpdatedBy(username);
                        appointmentEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
                        decreaseSlotBookedCapacity(appointmentEntity);
                    }
                });
                appointmentRepository.saveAll(appointmentEntityList);
                emailService.sendCancelNotification(appointmentEntityList, publicHoliday);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<List<PublicHolidayResponseDTO>> fetchPublicHolidaysByEmployeeId(Long employeeId) {
        EmployeeEntity employeeEntity = findEmployeeById(employeeId);
        try {
            List<PublicHolidayResponseDTO> publicHolidayResponseDTOList = new ArrayList<>();
            List<PublicHolidayEntity> publicHolidayEntityList = publicHolidayRepository.findByEmployeeId(employeeEntity);
            if (!publicHolidayEntityList.isEmpty()) {
                publicHolidayEntityList.forEach(publicHoliday -> publicHolidayResponseDTOList.add(new PublicHolidayResponseDTO(publicHoliday)));
            }
            return ResponseEntity.ok(publicHolidayResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private AppointmentStatusEntity findAppointmentStatusById(Integer appointmentStatusId) {
        Optional<AppointmentStatusEntity> appointmentStatusOptional = appointmentStatusRepository.findById(appointmentStatusId);
        if (!appointmentStatusOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.STATUS_NOT_FOUND);
        }
        return appointmentStatusOptional.get();
    }

    private void decreaseSlotBookedCapacity(AppointmentEntity appointmentEntity) {
        try {
            AvailableSlotsEntity availableSlotsEntity =
                    availableSlotsRepository.findByBranchIdAndServiceIdAndSlotDateAndStartTime(appointmentEntity.getBranchId(), appointmentEntity.getServiceId(), appointmentEntity.getAppointmentDate(), appointmentEntity.getStartTime()).get();
            availableSlotsEntity.setBookedCapacity(availableSlotsEntity.getBookedCapacity() - appointmentEntity.getPersonCount());
            if (!availableSlotsEntity.getBookedCapacity().equals(availableSlotsEntity.getTotalCapacity())) {
                availableSlotsEntity.setStatus(Constants.IN_PROGRESS);
            }
            if (availableSlotsEntity.getBookedCapacity() > 0) {
                availableSlotsRepository.save(availableSlotsEntity);
            } else {
                availableSlotsRepository.delete(availableSlotsEntity);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }
}
