package com.aptmgt.commons.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "appointment")
public class AppointmentEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @Basic
    @Temporal(TemporalType.DATE)
    @Column(name = "appointment_date")
    private Date appointmentDate;

    @Column(name = "start_time")
    private Time startTime;

    @Column(name = "end_time")
    private Time endTime;

    @Column(name = "description")
    private String description;

    @Column(name = "reason_for_cancel")
    private String reasonForCancel;

    @Column(name = "appointee_name")
    private String appointeeName;

    @Column(name = "appointee_email")
    private String appointeeEmail;

    @Column(name = "appointee_phone")
    private String appointeePhone;

    @Column(name = "person_count")
    private Integer personCount;

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

    @ManyToOne
    @JoinColumn(name = "appointment_status_id", nullable = false)
    private AppointmentStatusEntity appointmentStatusId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customerId;
}
