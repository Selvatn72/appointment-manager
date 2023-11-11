package com.aptmgt.admin.controllers;

import com.aptmgt.admin.dto.WeekHoursReqDTO;
import com.aptmgt.admin.dto.WeekendHoursResponseDTO;
import com.aptmgt.admin.services.IWeekEndHoursService;
import com.aptmgt.commons.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping("/weekend_hours")
public class WeekEndHoursController {

    @Autowired
    private IWeekEndHoursService iWeekEndHoursService;

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> saveWeekDayHours(@Valid @RequestBody WeekHoursReqDTO request) {
        return iWeekEndHoursService.saveWeekEndHour(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> updateWeekEndHrs(@PathVariable("id") Long id, @Valid @RequestBody WeekHoursReqDTO request) {
        return iWeekEndHoursService.updateWeekEndHrs(id, request);
    }

    @GetMapping("/branch/{branchId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<List<WeekendHoursResponseDTO>> fetchWeekEndHoursByBranchId(@PathVariable("branchId") Long branchId) {
        return iWeekEndHoursService.fetchWeekEndHoursByBranchId(branchId);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<List<WeekendHoursResponseDTO>> fetchWeekEndHoursByEmployeeId(@PathVariable("employeeId") Long employeeId) {
        return iWeekEndHoursService.fetchWeekEndHoursByEmployeeId(employeeId);
    }
}
