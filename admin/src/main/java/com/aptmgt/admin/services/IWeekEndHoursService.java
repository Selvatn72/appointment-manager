package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.WeekHoursReqDTO;
import com.aptmgt.admin.dto.WeekendHoursResponseDTO;
import com.aptmgt.commons.dto.MessageDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IWeekEndHoursService {

    ResponseEntity<MessageDTO> saveWeekEndHour(WeekHoursReqDTO weekEndHours);

    ResponseEntity<MessageDTO> updateWeekEndHrs(Long id, WeekHoursReqDTO weekEndHours);

    ResponseEntity<List<WeekendHoursResponseDTO>> fetchWeekEndHoursByBranchId(Long branchId);

    ResponseEntity<List<WeekendHoursResponseDTO>> fetchWeekEndHoursByEmployeeId(Long employeeId);
}
