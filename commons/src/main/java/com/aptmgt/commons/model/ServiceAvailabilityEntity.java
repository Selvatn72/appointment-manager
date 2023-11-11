package com.aptmgt.commons.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "service_availability")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAvailabilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_availability_id")
    private Long serviceAvailabilityId;

    @Column(name = "slot_time")
    private String slotTime;

    @Column(name = "slot_day")
    private String slotDay;

    @Column(name = "slot_interval")
    private Integer slotInterval;

    @Column(name = "slot_capacity")
    private Integer slotCapacity;

    @Column(name = "created_by")
    protected String createdBy;

    @Column(name = "created_date")
    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    protected Timestamp createdDate;

    @Column(name = "updated_by")
    protected String updatedBy;

    @Column(name = "updated_date", insertable = false)
    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    protected Timestamp updatedDate;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employeeId;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private BranchEntity branchId;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity serviceId;

}
