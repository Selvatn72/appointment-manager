package com.aptmgt.admin.services;

import com.aptmgt.admin.dto.AppointmentListReqDTO;
import com.aptmgt.admin.dto.StatusUpdateRequestDTO;
import com.aptmgt.commons.dto.MessageDTO;
import com.aptmgt.commons.dto.ResponseDTO;
import com.aptmgt.commons.model.AppointmentEntity;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAppointmentService {

    ResponseEntity<ResponseDTO> fetchAppointmentByBranchId(AppointmentListReqDTO request);

    ResponseEntity<MessageDTO> updateAppointmentStatus(StatusUpdateRequestDTO request);

    ResponseEntity<MessageDTO> cancelAppointment(StatusUpdateRequestDTO request);

    ResponseEntity<ResponseDTO> fetchAppointmentByEmployeeId(AppointmentListReqDTO request);

    List<AppointmentEntity> abandonedAppointmentsList();

    List<AppointmentEntity> saveAllAppointments(List<AppointmentEntity> appointmentEntities);

    ResponseEntity<Object> getCustomer(JSONObject request);
}
