package com.aptmgt.admin.controllers;

import com.aptmgt.admin.dto.AppointmentListReqDTO;
import com.aptmgt.admin.dto.StatusUpdateRequestDTO;
import com.aptmgt.admin.services.IAppointmentService;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.dto.ResponseDTO;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private IAppointmentService appointmentService;

    @PostMapping("/list")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<ResponseDTO> fetchAppointmentByBranchId(@Valid @RequestBody AppointmentListReqDTO request) {
        return appointmentService.fetchAppointmentByBranchId(request);
    }

    @PutMapping("/update_status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> updateAppointmentStatus(@Valid @RequestBody StatusUpdateRequestDTO request) {
        return appointmentService.updateAppointmentStatus(request);
    }

    @PutMapping("/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> cancelAppointment(@Valid @RequestBody StatusUpdateRequestDTO request) {
        return appointmentService.cancelAppointment(request);
    }

    @PostMapping("/employeeId")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<ResponseDTO> fetchAppointmentByEmployeeId(@Valid @RequestBody AppointmentListReqDTO request) {
        return appointmentService.fetchAppointmentByEmployeeId(request);
    }

    @PostMapping("/get_customer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<Object> getCustomer(@RequestBody JSONObject request) {
        return appointmentService.getCustomer(request);
    }

}