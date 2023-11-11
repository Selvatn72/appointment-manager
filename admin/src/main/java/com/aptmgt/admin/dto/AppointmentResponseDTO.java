package com.aptmgt.admin.dto;

import com.aptmgt.commons.model.AppointmentEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {

    private Long appointmentId;
    private Long employeeId;
    private String employeeName;
    private String serviceName;
    private Date appointmentDate;
    private Time startTime;
    private Time endTime;
    private String customerName;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdDate;
    private String phone;

    public AppointmentResponseDTO(AppointmentEntity appointment) {
        this.appointmentId = appointment.getAppointmentId();
        this.employeeId = appointment.getEmployeeId() != null ? appointment.getEmployeeId().getEmployeeId() : null;
        this.employeeName = appointment.getEmployeeId() != null ?appointment.getEmployeeId().getName() : null;
        this.appointmentDate = appointment.getAppointmentDate();
        this.serviceName = appointment.getServiceId().getServiceName();
        this.startTime = appointment.getStartTime();
        this.endTime = appointment.getEndTime();
        this.customerName = appointment.getCustomerId().getName();
        this.createdDate = appointment.getCreatedDate();
        this.phone = appointment.getCustomerId().getPhone();
        this.status = appointment.getAppointmentStatusId().getStatus();
    }
}
