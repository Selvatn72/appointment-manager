package com.aptmgt.admin.controllers;

import com.aptmgt.admin.dto.PublicHolidayResponseDTO;
import com.aptmgt.admin.dto.PublicHolidayRequestDTO;
import com.aptmgt.admin.services.IPublicHolidayService;
import com.aptmgt.commons.dto.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*",maxAge = 3600)
@RequestMapping("/public_holidays")
public class PublicHolidayController {

    @Autowired
    private IPublicHolidayService iPublicHolidayService;

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> savePublicHoliday(Authentication authentication,  @Valid @RequestBody PublicHolidayRequestDTO publicHoliday) {
        //Updating the cancel status for the appointments against the given holiday
        iPublicHolidayService.updateAppointments(publicHoliday, authentication);
        return iPublicHolidayService.savePublicHoliday(publicHoliday);
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<PublicHolidayResponseDTO> fetchByPublicHolidayId(@RequestParam("id") Long id) {
        return iPublicHolidayService.fetchByPublicHolidayId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> updatePublicHoliday(Authentication authentication, @PathVariable("id") Long id, @Valid @RequestBody PublicHolidayRequestDTO publicHoliday) {
        //Updating the cancel status for the appointments against the given holiday
        iPublicHolidayService.updateAppointments(publicHoliday, authentication);
        return iPublicHolidayService.updatePublicHoliday(id, publicHoliday);
    }

    @DeleteMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> deletePublicHoliday(@RequestParam("id") Long id) {
        return iPublicHolidayService.deletePublicHoliday(id);
    }

    @GetMapping("/branch")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<List<PublicHolidayResponseDTO>> fetchPublicHolidaysByBranchId(@RequestParam("id") Long branchId) {
        return iPublicHolidayService.fetchPublicHolidaysByBranchId(branchId);
    }

    @GetMapping("/employee")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<List<PublicHolidayResponseDTO>> fetchPublicHolidaysByEmployeeId(@RequestParam("id") Long employeeId) {
        return iPublicHolidayService.fetchPublicHolidaysByEmployeeId(employeeId);
    }
}