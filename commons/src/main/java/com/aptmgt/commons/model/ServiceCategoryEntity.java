package com.aptmgt.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "service_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_category_id")
    private Long serviceCategoryId;

    @Column(name = "service_category")
    private String serviceCategory;

}

