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
@Table(name = "service_request")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_request_id")
    private Long serviceRequestId;

    @Column(name = "service_name")
    private String serviceName;

    @Column(name = "status")
    private String status;

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
    @JoinColumn(name = "admin_id")
    private AdminEntity adminId;

    @ManyToOne
    @JoinColumn(name = "service_category_id")
    private ServiceCategoryEntity serviceCategoryId;
}
