package com.taskify.models;

import com.taskify.constants.ModelConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Table(name = ModelConstants.PARENT_COMPANY_TABLE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ParentCompanyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String pincode;

    @Column(nullable = false)
    private String headOfficeAddress;

    @Column(nullable = false)
    private String personOfContact;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String businessType;

    @Column(nullable = true)
    private String remarks;
    

}
