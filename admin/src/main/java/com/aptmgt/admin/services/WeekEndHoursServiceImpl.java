package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.WeekHoursReqDTO;
import com.aptmgt.admin.dto.WeekendHoursResponseDTO;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.exceptions.InternalServerErrorException;
import com.aptmgt.commons.exceptions.RecordNotFoundException;
import com.aptmgt.commons.model.BranchEntity;
import com.aptmgt.commons.model.EmployeeEntity;
import com.aptmgt.commons.model.TradingHoursEntity;
import com.aptmgt.commons.repository.BranchRepository;
import com.aptmgt.commons.repository.EmployeeRepository;
import com.aptmgt.commons.repository.TradingHoursRepository;
import com.aptmgt.commons.utils.ResponseMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class WeekEndHoursServiceImpl implements IWeekEndHoursService {

    @Autowired
    private TradingHoursRepository tradingHoursRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private IServiceAvailability iServiceAvailability;

    private static final Logger LOGGER = LoggerFactory.getLogger(WeekEndHoursServiceImpl.class);

    @Override
    public ResponseEntity<MessageDTO> saveWeekEndHour(WeekHoursReqDTO weekEndHours) {
        TradingHoursEntity tradingHoursEntity = new TradingHoursEntity();
        tradingHoursEntity.setBranchId(findBranchById(weekEndHours.getBranchId()));
        tradingHoursEntity.setEmployeeId(weekEndHours.getEmployeeId() != null ? findEmployeeById(weekEndHours.getEmployeeId()) : null);
        try {
            tradingHoursEntity.setStartTime(weekEndHours.getStartTime());
            tradingHoursEntity.setEndTime(weekEndHours.getEndTime());
            tradingHoursEntity.setIsWeekEnd(true);
            tradingHoursEntity.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            tradingHoursEntity.setCreatedDate(new Timestamp(new Date().getTime()));
            tradingHoursRepository.save(tradingHoursEntity);
            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.WEEK_END_HRS_CREATED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new InternalServerErrorException();
        }
    }

    private TradingHoursEntity findWeekEndHrsById(Long id) {
        Optional<TradingHoursEntity> tradingHoursEntityOptional = tradingHoursRepository.findById(id);
        if (!tradingHoursEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.WEEK_DAY_HRS_NOT_FOUND);
        }
        return tradingHoursEntityOptional.get();
    }

    @Override
    public ResponseEntity<MessageDTO> updateWeekEndHrs(Long id, WeekHoursReqDTO weekEndHours) {
        TradingHoursEntity tradingHoursEntity = findWeekEndHrsById(id);
        try {
            tradingHoursEntity.setStartTime(weekEndHours.getStartTime());
            tradingHoursEntity.setEndTime(weekEndHours.getEndTime());
            tradingHoursEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            tradingHoursEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            tradingHoursEntity = tradingHoursRepository.save(tradingHoursEntity);
            System.out.println(tradingHoursEntity);

            //Updating the weekend days service availability
           iServiceAvailability.updateWeekendDaysServiceAvailability(tradingHoursEntity);

            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.WEEK_END_HRS_UPDATED_MSG));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }

    public BranchEntity findBranchById(Long branchId) {
        Optional<BranchEntity> branchEntityOptional = branchRepository.findById(branchId);
        if (!branchEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.BRANCH_NOT_FOUND);
        }
        return branchEntityOptional.get();
    }

    @Override
    public ResponseEntity<List<WeekendHoursResponseDTO>> fetchWeekEndHoursByBranchId(Long branchId) {
        BranchEntity branchEntity = findBranchById(branchId);
        try {
            List<WeekendHoursResponseDTO> weekendHoursResponseDTOList = new ArrayList<>();
            List<TradingHoursEntity> tradingHoursEntityList = tradingHoursRepository.findByBranchIdAndIsWeekEnd(branchEntity, true);
            if (!tradingHoursEntityList.isEmpty()) {
                tradingHoursEntityList.forEach(weekEndHours -> weekendHoursResponseDTOList.add(new WeekendHoursResponseDTO(weekEndHours)));
            }
            return ResponseEntity.ok(weekendHoursResponseDTOList);
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

    @Override
    public ResponseEntity<List<WeekendHoursResponseDTO>> fetchWeekEndHoursByEmployeeId(Long employeeId) {
        EmployeeEntity employeeEntity = findEmployeeById(employeeId);
        try {
            List<WeekendHoursResponseDTO> weekendHoursResponseDTOList = new ArrayList<>();
            List<TradingHoursEntity> tradingHoursEntityList = tradingHoursRepository.findByEmployeeIdAndIsWeekEnd(employeeEntity, true);
            if (!tradingHoursEntityList.isEmpty()) {
                tradingHoursEntityList.forEach(weekEndHours -> weekendHoursResponseDTOList.add(new WeekendHoursResponseDTO(weekEndHours)));
            }
            return ResponseEntity.ok(weekendHoursResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }
}

