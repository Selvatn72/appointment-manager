package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.AppointmentListReqDTO;
import com.aptmgt.admin.dto.AppointmentResponseDTO;
import com.aptmgt.admin.dto.StatusUpdateRequestDTO;
import com.aptmgt.admin.services.email.EmailService;
import com.aptmgt.commons.model.*;
import com.aptmgt.commons.repository.*;
import com.aptmgt.commons.dto.CustomerResDTO;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.dto.ResponseDTO;
import com.aptmgt.commons.exceptions.BadRequestException;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.utils.Constants;
import com.aptmgt.commons.utils.DateUtils;
import com.aptmgt.commons.utils.ResponseMessages;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements IAppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private AppointmentStatusRepository appointmentStatusRepository;

    @Autowired
    private AvailableSlotsRepository availableSlotsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomerRepository customerRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private BranchEntity findBranchById(Long branchId) {
        Optional<BranchEntity> branchEntityOptional = branchRepository.findById(branchId);
        if (!branchEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.BRANCH_NOT_FOUND);
        }
        return branchEntityOptional.get();
    }

    @Override
    public ResponseEntity<ResponseDTO> fetchAppointmentByBranchId(AppointmentListReqDTO request) {
        List<AppointmentResponseDTO> appointmentResponseDTOList = new ArrayList<>();
        Page<AppointmentEntity> appointmentEntityList;

        AppointmentStatusEntity appointmentStatus =  request.getStatusId() != null ? findAppointmentStatusById(request.getStatusId()) : null;
        BranchEntity branchEntity = findBranchById(request.getBranchId());
        Optional<CustomerEntity> customer = request.getMobileNumber() != null
                ? customerRepository.findByPhone(request.getMobileNumber()) : null;

        try {
            Pageable paging = PageRequest.of(request.getPageNo() - 1, request.getPageSize(), Sort.by("appointmentDate").ascending().and(Sort.by("startTime").ascending()));
            if (customer != null && customer.isPresent()) {
                appointmentEntityList = appointmentRepository.findByBranchIdAndCustomerId(branchEntity, customer.get(), paging);
            } else if(request.getStartDate() != null && request.getEndDate() != null) {
                if(appointmentStatus != null) {
                    appointmentEntityList = appointmentRepository.findByBranchIdAndAppointmentDateBetweenAndAppointmentStatusId(branchEntity, request.getStartDate(), request.getEndDate(), appointmentStatus, paging);
                } else {
                    appointmentEntityList = appointmentRepository.findByBranchIdAndAppointmentDateBetween(branchEntity, request.getStartDate(), request.getEndDate(), paging);
                }
            } else {
                Date previousDate = DateUtils.getPreviousDate(request.getDate(), 30);
                if(appointmentStatus != null) {
                    appointmentEntityList = appointmentRepository.findByBranchIdAndAppointmentDateBetweenAndAppointmentStatusId(branchEntity, request.getDate(), previousDate, appointmentStatus, paging);
                } else {
                    appointmentEntityList = appointmentRepository.findByBranchIdAndAppointmentDateBetween(branchEntity, request.getDate(), previousDate, paging);
                }
            }

            if (!appointmentEntityList.isEmpty()) {
                appointmentEntityList.forEach(appointment -> appointmentResponseDTOList.add(new AppointmentResponseDTO(appointment)));
            }
            return ResponseEntity.ok(new ResponseDTO(ResponseMessages.SUCCESS, true, appointmentResponseDTOList, appointmentEntityList.getTotalElements()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<MessageDTO> updateAppointmentStatus(StatusUpdateRequestDTO request) {
        AppointmentEntity appointmentEntity = findAppointmentById(request.getAppointmentId());
        try {
            appointmentEntity.setAppointmentStatusId(findAppointmentStatusById(request.getStatusId()));
            appointmentEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            appointmentEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            appointmentRepository.save(appointmentEntity);

            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.APPOINTMENT_STATUS_UPDATE_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    public AppointmentEntity findAppointmentById(Long appointmentId) {
        Optional<AppointmentEntity> appointmentEntity = appointmentRepository.findById(appointmentId);
        if (!appointmentEntity.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.APPOINTMENT_NOT_FOUND);
        }
        return appointmentEntity.get();
    }

    private AppointmentStatusEntity findAppointmentStatusById(Integer appointmentStatusId) {
        Optional<AppointmentStatusEntity> appointmentStatusOptional = appointmentStatusRepository.findById(appointmentStatusId);
        if (!appointmentStatusOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.STATUS_NOT_FOUND);
        }
        return appointmentStatusOptional.get();
    }

    @Override
    public ResponseEntity<MessageDTO> cancelAppointment(StatusUpdateRequestDTO request) {
        AppointmentEntity appointmentEntity = findAppointmentById(request.getAppointmentId());

        decreaseSlotBookedCapacity(appointmentEntity);

        appointmentEntity.setAppointmentStatusId(findAppointmentStatusById(5));
        try {
            appointmentEntity.setReasonForCancel(request.getReasonForCancel());
            appointmentEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            appointmentEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            appointmentRepository.save(appointmentEntity);

            if (appointmentEntity.getAppointeeEmail() != null) {
                emailService.sendCancelNotification(appointmentEntity);
            }

            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.APPOINTMENT_CANCELLED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> fetchAppointmentByEmployeeId(AppointmentListReqDTO request) {
        EmployeeEntity employeeEntity = findEmployeeById(request.getEmployeeId());
        List<AppointmentResponseDTO> appointmentResponseDTOList = new ArrayList<>();
        Page<AppointmentEntity> appointmentEntityList;

        AppointmentStatusEntity appointmentStatus =  request.getStatusId() != null ? findAppointmentStatusById(request.getStatusId()) : null;
        Optional<CustomerEntity> customer = request.getMobileNumber() != null
                ? customerRepository.findByPhone(request.getMobileNumber()) : null;

        try {
            Pageable paging = PageRequest.of(request.getPageNo() - 1, request.getPageSize(), Sort.by("appointmentDate").ascending().and(Sort.by("startTime").ascending()));
            if (customer != null && customer.isPresent()) {
                appointmentEntityList = appointmentRepository.findByEmployeeIdAndCustomerId(employeeEntity, customer.get(), paging);
            } else if(request.getStartDate() != null && request.getEndDate() != null) {
                if(appointmentStatus != null) {
                    appointmentEntityList = appointmentRepository.findByEmployeeIdAndAppointmentDateBetweenAndAppointmentStatusId(employeeEntity, request.getStartDate(), request.getEndDate(), appointmentStatus, paging);
                } else {
                    appointmentEntityList = appointmentRepository.findByEmployeeIdAndAppointmentDateBetween(employeeEntity, request.getStartDate(), request.getEndDate(), paging);
                }
            } else {
                Date previousDate = DateUtils.getPreviousDate(request.getDate(), 30);
                if(appointmentStatus != null) {
                    appointmentEntityList = appointmentRepository.findByEmployeeIdAndAppointmentDateBetweenAndAppointmentStatusId(employeeEntity, request.getDate(), previousDate, appointmentStatus, paging);
                } else {
                    appointmentEntityList = appointmentRepository.findByEmployeeIdAndAppointmentDateBetween(employeeEntity, request.getDate(), previousDate, paging);
                }
            }

            if (!appointmentEntityList.isEmpty()) {
                appointmentEntityList.forEach(appointment -> appointmentResponseDTOList.add(new AppointmentResponseDTO(appointment)));
            }
            return ResponseEntity.ok(new ResponseDTO(ResponseMessages.SUCCESS, true, appointmentResponseDTOList, appointmentEntityList.getTotalElements()));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public List<AppointmentEntity> abandonedAppointmentsList() {
        return appointmentRepository.findAllAbandonedAppointments();
    }

    @Override
    public List<AppointmentEntity> saveAllAppointments(List<AppointmentEntity> appointmentEntities) {
        return appointmentRepository.saveAll(appointmentEntities);
    }

    private EmployeeEntity findEmployeeById(Long employeeId) {
        Optional<EmployeeEntity> employeeEntityOptional = employeeRepository.findById(employeeId);
        if (!employeeEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.EMPLOYEE_NOT_FOUND);
        }
        return employeeEntityOptional.get();
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

    @Override
    public ResponseEntity<Object> getCustomer(JSONObject request) {
        if (request.get("phone") == null) {
            throw new BadRequestException(ResponseMessages.MOBILE_NUMBER_NOT_FOUND);
        }
        try {
            Optional<CustomerEntity> customerEntity = customerRepository.findByPhone(request.get("phone").toString());
            if(customerEntity.isPresent()) {
                return ResponseEntity.ok(new ResponseDTO(ResponseMessages.SUCCESS, true, new CustomerResDTO(customerEntity.get()), null));
            } else {
                return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.SUCCESS, true));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }
}
