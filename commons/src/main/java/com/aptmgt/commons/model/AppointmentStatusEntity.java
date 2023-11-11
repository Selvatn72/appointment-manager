package com.aptmgt.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "appointment_status")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_status_id")
    private Integer appointmentStatusId;

    @Column(name = "status")
    private String status;

}