package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.WeekHoursReqDTO;
import com.aptmgt.admin.dto.WeekdayHoursResponseDTO;
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
public class WeekDaysHoursServiceImpl implements IWeekDaysHoursService {

    @Autowired
    private TradingHoursRepository tradingHoursRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private IServiceAvailability iServiceAvailability;

    private static final Logger LOGGER = LoggerFactory.getLogger(WeekDaysHoursServiceImpl.class);

    @Override
    public ResponseEntity<MessageDTO> saveWeekDaysHour(WeekHoursReqDTO weekDaysHours) {
        TradingHoursEntity tradingHoursEntity = new TradingHoursEntity();
        tradingHoursEntity.setBranchId(findBranchById(weekDaysHours.getBranchId()));
        tradingHoursEntity.setEmployeeId(weekDaysHours.getEmployeeId() != null ? findEmployeeById(weekDaysHours.getEmployeeId()) : null);
        try {
            tradingHoursEntity.setStartTime(weekDaysHours.getStartTime());
            tradingHoursEntity.setEndTime(weekDaysHours.getEndTime());
            tradingHoursEntity.setIsWeekDay(true);
            tradingHoursEntity.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            tradingHoursEntity.setCreatedDate(new Timestamp(new Date().getTime()));
            tradingHoursRepository.save(tradingHoursEntity);
            return ResponseEntity.ok((new MessageDTO(true, ResponseMessages.WEEK_DAY_HRS_CREATED_MSG)));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
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

    private TradingHoursEntity findWeekDayHrsById(Long id) {
        Optional<TradingHoursEntity> tradingHoursEntityOptional = tradingHoursRepository.findById(id);
        if (!tradingHoursEntityOptional.isPresent()) {
            throw new RecordNotFoundException(ResponseMessages.WEEK_DAY_HRS_NOT_FOUND);
        }
        return tradingHoursEntityOptional.get();
    }

    @Override
    public ResponseEntity<MessageDTO> updateWeekDaysHrs(Long id, WeekHoursReqDTO weekDaysHours) {
        TradingHoursEntity tradingHoursEntity = findWeekDayHrsById(id);
        try {
            tradingHoursEntity.setStartTime(weekDaysHours.getStartTime());
            tradingHoursEntity.setEndTime(weekDaysHours.getEndTime());
            tradingHoursEntity.setUpdatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
            tradingHoursEntity.setUpdatedDate(new Timestamp(new Date().getTime()));
            tradingHoursEntity = tradingHoursRepository.save(tradingHoursEntity);

            //Updating the week days service availability
            iServiceAvailability.updateWeekDaysServiceAvailability(tradingHoursEntity);

            return ResponseEntity.ok(new MessageDTO(true, ResponseMessages.WEEK_DAY_HRS_UPDATED_MSG));
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
    public ResponseEntity<List<WeekdayHoursResponseDTO>> fetchWeekDaysHoursByBranchId(Long branchId) {
        BranchEntity branchEntity = findBranchById(branchId);
        try {
            List<WeekdayHoursResponseDTO> weekdayHoursResponseDTOList = new ArrayList<>();
            List<TradingHoursEntity> tradingHoursEntityList = tradingHoursRepository.findByBranchIdAndIsWeekDay(branchEntity, true);
            if (!tradingHoursEntityList.isEmpty()) {
                tradingHoursEntityList.forEach(weekDaysHours -> weekdayHoursResponseDTOList.add(new WeekdayHoursResponseDTO(weekDaysHours)));
            }
            return ResponseEntity.ok(weekdayHoursResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }

    @Override
    public ResponseEntity<List<WeekdayHoursResponseDTO>> fetchWeekDaysHoursByEmployeeId(Long employeeId) {
        EmployeeEntity employeeEntity = findEmployeeById(employeeId);
        try {
            List<WeekdayHoursResponseDTO> weekdayHoursResponseDTOList = new ArrayList<>();
            List<TradingHoursEntity> tradingHoursEntityList = tradingHoursRepository.findByEmployeeIdAndIsWeekDay(employeeEntity, true);
            if (!tradingHoursEntityList.isEmpty()) {
                tradingHoursEntityList.forEach(weekDaysHours -> weekdayHoursResponseDTOList.add(new WeekdayHoursResponseDTO(weekDaysHours)));
            }
            return ResponseEntity.ok(weekdayHoursResponseDTOList);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new InternalServerErrorException();
        }
    }
}
