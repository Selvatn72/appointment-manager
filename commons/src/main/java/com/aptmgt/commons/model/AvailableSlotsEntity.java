package com.aptmgt.commons.model;

import lombok.*;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "available_slot")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableSlotsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "available_slot_id")
    private Long availableSlotId;

    @Basic
    @Temporal(TemporalType.DATE)
    @Column(name = "slot_date")
    private Date slotDate;

    @Column(name="start_time")
    private Time startTime;

    @Column(name = "end_time")
    private Time endTime;

    @Column(name = "booked_capacity")
    private Integer bookedCapacity;

    @Column(name = "total_capacity")
    private Integer totalCapacity;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employeeId;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity serviceId;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private BranchEntity branchId;

    @Column(name = "status")
    private String status;
}
