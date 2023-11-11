package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.ServiceAvailabilityRequestDTO;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.dto.ServiceAvailabilityResponseDTO;
import com.aptmgt.commons.model.*;
import com.aptmgt.commons.exceptions.BadRequestException;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.repository.BranchRepository;
import com.aptmgt.commons.repository.EmployeeRepository;
import com.aptmgt.commons.repository.ServiceAvailabilityRepository;
import com.aptmgt.commons.repository.ServiceRepository;
import com.aptmgt.commons.utils.Constants;
import com.aptmgt.commons.utils.DateUtils;
import com.aptmgt.commons.utils.ResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceAvailabilityServiceImpl implements IServiceAvailability {

    @Autowired
    private ServiceAvailabilityRepository serviceAvailabilityRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAvailabilityServiceImpl.class);

    @Override
    public ResponseEntity<MessageDTO> saveServiceAvailability(ServiceAvailabilityRequestDTO request) {
        if (request.getSlotDetails().isEmpty()) {
            throw new BadRequestException(ResponseMessages.SLOT_REQUEST_CANNOT_EMPTY);
        }
        List<ServiceAvailabilityEntity> serviceAvailabilityEntityList = new ArrayList<>();
        for (int i = 0; i < request.getSlotDetails().size(); i++) {
            if (request.getSlotDetails().get(i).getServiceId() == null) {
                throw new BadRequestException(ResponseMessages.SERVICE_ID_NOT_NULL);
            }
            serviceAvailabilityEntityList.add(new ServiceAvailabilityEntity(
                    request.getSlotDetails().get(i).getServiceAvailabilityId(),
                    request.getSlotDetails().get(i).getSlotTime(),
                    request.getSlotDetails().get(i).getSlotDay(),
                    request.getSlotDetails().get(i).getSlotInterval(),
                    request.getSlotDetails().get(i).getSlotCapacity(),
                    SecurityContextHolder.getContext().getAuthentication().getName(),
                    new Timestamp(new Date().getTime()),
                    request.getSlotDetails().get(i).getServiceAvailabilityId() != null ? SecurityContextHolder.getContext().getAuthentication().getName() : null,
                    request.getSlotDetails().get(i).getServiceAvailabilityId() != null ? new Timestamp(new Date().getTime()) : null,
                    request.getEmployeeId() != null ? findEmployeeById(request.getEmployeeId()) : null,
                    findBranchById(request.getBranchId()),
                    findServiceById(request.getSlotDetails().get(i).getServiceId())
            ));
            serviceAvailabilityRepository.saveAll(serviceAvailabilityEntityList);
        }
        return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.SLOT_CONFIG_MSG));
    }

    private EmployeeEntity findEmployeeById(Long employeeId) {
        Optional<EmployeeEntity> employeeEntityOptional = employeeRepository.findById(employeeId);
        if (!employeeEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.EMPLOYEE_NOT_FOUND);
        }
        return employeeEntityOptional.get();
    }

    private BranchEntity findBranchById(Long branchId) {
        Optional<BranchEntity> branchEntityOptional = branchRepository.findById(branchId);
        if (!branchEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.BRANCH_NOT_FOUND);
        }
        return branchEntityOptional.get();
    }

    private ServiceEntity findServiceById(Long serviceId) {
        Optional<ServiceEntity> serviceEntityOptional = serviceRepository.findById(serviceId);
        if (!serviceEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_NOT_FOUND);
        }
        return serviceEntityOptional.get();
    }

    @Override
    public ResponseEntity<MessageDTO> deleteServiceAvailability(Long serviceAvailabilityId) {
        ServiceAvailabilityEntity serviceAvailabilityEntity = findServiceAvailabilityById(serviceAvailabilityId);
        try {
            serviceAvailabilityEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            serviceAvailabilityEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            serviceAvailabilityRepository.save(serviceAvailabilityEntity);

            serviceAvailabilityRepository.deleteById(serviceAvailabilityId);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.SERVICE_AVAILABILITY_DELETED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private ServiceAvailabilityEntity findServiceAvailabilityById(Long serviceAvailabilityId) {
        Optional<ServiceAvailabilityEntity> serviceAvailabilityEntityOptional = serviceAvailabilityRepository.findById(serviceAvailabilityId);
        if (!serviceAvailabilityEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.SERVICE_AVAILABILITY_NOT_FOUND);
        }
        return serviceAvailabilityEntityOptional.get();
    }

    @Override
    public ResponseEntity<List<ServiceAvailabilityResponseDTO>> getServiceAvailabilityByEmployeeId(Long employeeId) {
        EmployeeEntity employeeEntity = findEmployeeById(employeeId);
        try {
            List<ServiceAvailabilityResponseDTO> serviceAvailabilityResDTOList = new ArrayList<>();
            List<ServiceAvailabilityEntity> serviceAvailabilityEntityList = serviceAvailabilityRepository.findByEmployeeId(employeeEntity);
            if (!serviceAvailabilityEntityList.isEmpty()) {
                serviceAvailabilityEntityList.forEach(serviceAvailabilityEntity -> serviceAvailabilityResDTOList.add(new ServiceAvailabilityResponseDTO(serviceAvailabilityEntity)));
            }
            return ResponseEntity.ok(serviceAvailabilityResDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<List<ServiceAvailabilityResponseDTO>> getServiceAvailabilityByBranchId(Long branchId) {
        BranchEntity branchEntity = findBranchById(branchId);
        try {
            List<ServiceAvailabilityResponseDTO> serviceAvailabilityResDTOList = new ArrayList<>();
            List<ServiceAvailabilityEntity> serviceAvailabilityEntityList = serviceAvailabilityRepository.findByBranchId(branchEntity);
            if (!serviceAvailabilityEntityList.isEmpty()) {
                serviceAvailabilityEntityList.forEach(serviceAvailabilityEntity -> serviceAvailabilityResDTOList.add(new ServiceAvailabilityResponseDTO(serviceAvailabilityEntity)));
            }
            return ResponseEntity.ok(serviceAvailabilityResDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Async
    @Override
    public void updateWeekDaysServiceAvailability(TradingHoursEntity tradingHoursEntity) {
        try {
            List<ServiceAvailabilityEntity> serviceAvailabilityEntityList = new ArrayList<>();
            List<ServiceAvailabilityEntity> existingServiceAvailabilityEntityList = serviceAvailabilityRepository.findByBranchIdAndSlotDayIn(tradingHoursEntity.getBranchId(), Constants.WEEK_DAYS);
            if (!existingServiceAvailabilityEntityList.isEmpty()) {
                String slotTime = DateUtils.get12HrsFrom24Hrs(tradingHoursEntity.getStartTime()) + "-" + DateUtils.get12HrsFrom24Hrs(tradingHoursEntity.getEndTime());
                existingServiceAvailabilityEntityList.forEach(serviceAvailabilityEntity -> {
                    serviceAvailabilityEntity.setSlotTime(slotTime);
                    serviceAvailabilityEntityList.add(serviceAvailabilityEntity);
                });
                serviceAvailabilityRepository.saveAll(serviceAvailabilityEntityList);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    @Async
    @Override
    public void updateWeekendDaysServiceAvailability(TradingHoursEntity tradingHoursEntity) {
        try {
            List<ServiceAvailabilityEntity> serviceAvailabilityEntityList = new ArrayList<>();
            List<ServiceAvailabilityEntity> existingServiceAvailabilityEntityList = serviceAvailabilityRepository.findByBranchIdAndSlotDayIn(tradingHoursEntity.getBranchId(), Constants.WEEK_END_DAYS);
            if (!existingServiceAvailabilityEntityList.isEmpty()) {
                String slotTime = DateUtils.get12HrsFrom24Hrs(tradingHoursEntity.getStartTime()) + "-" + DateUtils.get12HrsFrom24Hrs(tradingHoursEntity.getEndTime());
                existingServiceAvailabilityEntityList.forEach(serviceAvailabilityEntity -> {
                    serviceAvailabilityEntity.setSlotTime(slotTime);
                    serviceAvailabilityEntityList.add(serviceAvailabilityEntity);
                });
                serviceAvailabilityRepository.saveAll(serviceAvailabilityEntityList);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }
}
