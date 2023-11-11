package com.aptmgt.commons.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "employee")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "city")
    private String city;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "degree")
    private String degree;

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "image")
    @Lob
    private byte[] image;

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
    @JoinColumn(name = "service_id")
    private ServiceEntity serviceId;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private BranchEntity branchId;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "employee_sub_service", joinColumns = {
            @JoinColumn(name = "employee_id", referencedColumnName = "employee_id") }, inverseJoinColumns = {
            @JoinColumn(name = "sub_service_id", referencedColumnName = "sub_service_id") })
    private List<SubServiceEntity> subServices;
}
