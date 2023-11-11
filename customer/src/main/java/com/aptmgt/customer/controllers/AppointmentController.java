package com.aptmgt.customer.controllers;

import com.aptmgt.customer.dto.*;
import com.aptmgt.commons.dto.FrequentvisitResponseDTO;
import com.aptmgt.customer.dto.*;
import com.aptmgt.customer.services.IAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private IAppointmentService appointmentService;

    @PostMapping("/get_slots")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<SlotResponse> getSlots(@Valid @RequestBody SlotRequestDTO request) {
        return appointmentService.getSlots(request);
    }

    @PostMapping("/check_appointment")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<Object> fetchAppointmentByDateAndTime(@Valid @RequestBody AppointmentListRequestDTO appointmentListRequestDTO) {
        return appointmentService.fetchAppointmentByDateAndTime(appointmentListRequestDTO);
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
    public ResponseEntity<MessageDTO> saveAppointment(@Valid @RequestBody AppointmentRequestDTO appointmentRequestDTO) {
        return appointmentService.saveAppointment(appointmentRequestDTO);
    }

    @PostMapping("/get_appointments")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentDetails(@Valid @RequestBody AppointmentListRequestDTO appointmentListRequestDTO) {
        return appointmentService.getAppointmentDetails(appointmentListRequestDTO);
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<AppointmentResponseDTO> fetchAppointmentByAppointmentId(@PathVariable("appointmentId") Long appointmentId) {
        return appointmentService.fetchAppointmentByAppointmentId(appointmentId);
    }

    @PostMapping("/upcoming_time")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentUpcomingTime(@Valid @RequestBody AppointmentListRequestDTO  appointmentListDTO) {
        return appointmentService.getAppointmentUpcomingTime(appointmentListDTO);
    }

    @GetMapping("/frequent_visit/{customerId}")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<FrequentvisitResponseDTO>> fetchFrequentVisitList(@PathVariable("customerId") Long customerId) {
        return appointmentService.fetchFrequentVisitList(customerId);
    }

    @PutMapping("/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<MessageDTO> cancelAppointment(@Valid @RequestBody StatusUpdateRequestDTO request) {
        return appointmentService.cancelAppointment(request);
    }
}
