package com.aptmgt.customer.services;

import com.aptmgt.customer.dto.*;
import com.aptmgt.commons.dto.FrequentvisitResponseDTO;
import com.aptmgt.customer.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAppointmentService {

    ResponseEntity<MessageDTO> saveAppointment(AppointmentRequestDTO appointmentRequestDTO);

    ResponseEntity<List<AppointmentResponseDTO>>  getAppointmentDetails(AppointmentListRequestDTO appointmentListRequestDTO);

    ResponseEntity<MessageDTO> cancelAppointment(StatusUpdateRequestDTO request);

    ResponseEntity<List<AppointmentResponseDTO>> getAppointmentUpcomingTime(AppointmentListRequestDTO appointmentListDTO);

    ResponseEntity<SlotResponse> getSlots(SlotRequestDTO request);

    ResponseEntity<AppointmentResponseDTO> fetchAppointmentByAppointmentId(Long appointmentId);

    ResponseEntity<List<FrequentvisitResponseDTO>> fetchFrequentVisitList(Long customerId);

    ResponseEntity<Object> fetchAppointmentByDateAndTime(AppointmentListRequestDTO appointmentListRequestDTO);
}
