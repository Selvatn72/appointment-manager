package com.aptmgt.admin.controllers;

import com.aptmgt.admin.dto.WeekHoursReqDTO;
import com.aptmgt.admin.dto.WeekdayHoursResponseDTO;
import com.aptmgt.admin.services.IWeekDaysHoursService;
import com.aptmgt.commons.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping("/weekday_hours")
public class WeekDaysHoursController {

    @Autowired
    private IWeekDaysHoursService iWeekDaysHoursService;

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> saveWeekDayHours(@Valid @RequestBody WeekHoursReqDTO weekDaysHours) {
        return iWeekDaysHoursService.saveWeekDaysHour(weekDaysHours);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> updateWeekDaysHrs(@PathVariable("id") Long id, @RequestBody WeekHoursReqDTO weekDaysHours) {
        return iWeekDaysHoursService.updateWeekDaysHrs(id, weekDaysHours);
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<List<WeekdayHoursResponseDTO>> fetchWeekDaysHoursByBranchId(@PathVariable("branchId") Long branchId) {
        return iWeekDaysHoursService.fetchWeekDaysHoursByBranchId(branchId);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<List<WeekdayHoursResponseDTO>> fetchWeekDaysHoursByEmployeeId(@PathVariable("employeeId") Long employeeId) {
        return iWeekDaysHoursService.fetchWeekDaysHoursByEmployeeId(employeeId);
    }
}
