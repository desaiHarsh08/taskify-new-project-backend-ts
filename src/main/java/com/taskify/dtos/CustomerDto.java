package com.taskify.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private Long id;

    private String customerName;

    private String email;

    private String personOfContact;

    private String phone;

    private String state;

    private LocalDateTime birthDate = LocalDateTime.now();

    private LocalDateTime anniversary = LocalDateTime.now();

    private String address;

    private String residenceAddress;

    private String city;

    private String pincode;

    private List<TaskDto> allTask;

    private Long parentCompanyId;

}
