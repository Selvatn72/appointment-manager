package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.ServiceAvailabilityRequestDTO;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.dto.ServiceAvailabilityResponseDTO;
import com.aptmgt.commons.model.TradingHoursEntity;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.List;

public interface IServiceAvailability {

    ResponseEntity<MessageDTO> saveServiceAvailability(ServiceAvailabilityRequestDTO request);

    ResponseEntity<List<ServiceAvailabilityResponseDTO>> getServiceAvailabilityByBranchId(Long branchId);

    ResponseEntity<MessageDTO> deleteServiceAvailability(Long serviceAvailabilityId);

    ResponseEntity<List<ServiceAvailabilityResponseDTO>> getServiceAvailabilityByEmployeeId(Long employeeId);

    void updateWeekDaysServiceAvailability(TradingHoursEntity tradingHoursEntity) throws ParseException;

    void updateWeekendDaysServiceAvailability(TradingHoursEntity tradingHoursEntity);
}
