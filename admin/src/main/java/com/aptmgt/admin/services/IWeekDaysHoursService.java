package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.WeekHoursReqDTO;
import com.aptmgt.admin.dto.WeekdayHoursResponseDTO;
import com.aptmgt.commons.dto.MessageDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IWeekDaysHoursService {

     ResponseEntity<MessageDTO> saveWeekDaysHour(WeekHoursReqDTO weekDaysHours);

     ResponseEntity<MessageDTO> updateWeekDaysHrs(Long id, WeekHoursReqDTO weekDaysHours);

     ResponseEntity<List<WeekdayHoursResponseDTO>> fetchWeekDaysHoursByBranchId(Long branchId);

     ResponseEntity<List<WeekdayHoursResponseDTO>> fetchWeekDaysHoursByEmployeeId(Long employeeId);
}
