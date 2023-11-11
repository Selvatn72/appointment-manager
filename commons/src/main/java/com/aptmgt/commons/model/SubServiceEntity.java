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
@Table(name = "sub_service")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_service_id")
    private Long subServiceId;

    @Column(name = "name")
    private String name;

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
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity serviceId;
}
