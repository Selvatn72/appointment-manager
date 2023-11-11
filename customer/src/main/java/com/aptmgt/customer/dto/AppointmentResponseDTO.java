package com.aptmgt.customer.dto;

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

    private String serviceName;

    private Integer personCount;

    private String shopName;

    private String appointeePhone;


    private String appointeeName;

    private Date appointmentDate;

    private String appointmentStatus;

    private Time startTime;

    private Time endTime;

    private String status;

    private Integer statusId;

    private String customerName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdDate;

    private String description;

    private String customerPhone;

    private String branchPhone;

    private String address;

    private String cityName;

    private String stateName;

    private String countryName;

    private String zipcode;

    private Float latitude;

    private Float longitude;

    private Long employeeId;

    private String employeeName;

    private Long serviceCategoryId;


    public AppointmentResponseDTO(AppointmentEntity appointment) {
        this.appointmentId = appointment.getAppointmentId();
        this.appointeeName = appointment.getAppointeeName();
        this.appointmentDate = appointment.getAppointmentDate();
        this.appointmentStatus = appointment.getAppointmentStatusId().getStatus();
        this.personCount = appointment.getPersonCount();
        this.serviceName = appointment.getServiceId().getServiceName();
        this.shopName = appointment.getBranchId().getName();
        this.startTime = appointment.getStartTime();
        this.endTime = appointment.getEndTime();
        this.statusId = appointment.getAppointmentStatusId().getAppointmentStatusId();
        this.status = appointment.getAppointmentStatusId().getStatus();
        this.customerName = appointment.getCustomerId().getName();
        this.createdDate = appointment.getCreatedDate();
        this.description = appointment.getDescription();
        this.appointeePhone = appointment.getAppointeePhone();
        this.branchPhone = appointment.getBranchId().getPhone();
        this.address = appointment.getBranchId().getAddress();
        this.cityName = appointment.getBranchId().getCity();
        this.stateName = appointment.getBranchId().getState();
        this.serviceCategoryId = appointment.getServiceId().getServiceCategoryId().getServiceCategoryId();
        this.countryName = appointment.getBranchId().getCountry();
        this.zipcode = appointment.getBranchId().getZipcode();
        this.latitude = appointment.getBranchId().getLatitude();
        this.longitude = appointment.getBranchId().getLongitude();
        this.employeeId = appointment.getEmployeeId() != null ? appointment.getEmployeeId().getEmployeeId() : null;
        this.employeeName = appointment.getEmployeeId() != null ? appointment.getEmployeeId().getName() : null;
    }
}