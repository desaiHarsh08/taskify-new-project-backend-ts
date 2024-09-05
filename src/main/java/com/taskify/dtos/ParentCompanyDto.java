package com.taskify.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentCompanyDto {

    private Long id;

    private String companyName;

    private String address;

    private String state;

    private String city;

    private String pincode;

    private String headOfficeAddress;

    private String personOfContact;

    private String phone;

    private String businessType;

    private String remarks;

    private List<CustomerDto> customers;

}
