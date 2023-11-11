package com.aptmgt.commons.model;

import lombok.Data;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;

@Entity
@Table(name = "user_verification")
@Data
public class UserVerificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "verification_code")
    private String VerificationCode;

    @Column(name = "code_sent_on", columnDefinition="DATETIME")
    private Date codeSentOn = new Time(new Date().getTime());

    @Column(name = "status")
    private String status;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userId;
}