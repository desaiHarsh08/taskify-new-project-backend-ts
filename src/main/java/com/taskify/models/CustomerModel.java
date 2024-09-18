package com.taskify.models;

import java.time.LocalDateTime;

import com.taskify.constants.ModelConstants;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = ModelConstants.CUSTOMER_TABLE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String customerName;

    private String email;

    @Column(nullable = false)
    private String personOfContact;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String state;

    @Column(nullable = true)
    private LocalDateTime birthDate;

    @Column(nullable = true)
    private LocalDateTime anniversary;

    @Column(nullable = false)
    private String address;

    @Column(nullable = true)
    private String residenceAddress;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String pincode;

    @ManyToOne(targetEntity = ParentCompanyModel.class)
    private ParentCompanyModel parentCompany;

}
