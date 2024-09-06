package com.taskify.services;

import java.util.List;

import com.taskify.dtos.CustomerDto;
import com.taskify.utils.PageResponse;

public interface CustomerServices {

    CustomerDto createCustomer(CustomerDto customer);

    PageResponse<CustomerDto> getAllCustomers(int pageNumber);

    PageResponse<CustomerDto> getCustomersByEmailOrCityOrState(String email, String city, String state, int pageNumber);

    CustomerDto getCustomerById(Long customerId);

    CustomerDto getCustomerByEmail(String email);

    CustomerDto getCustomerByName(String customerName);

    List<CustomerDto> getCustomerByParentCompanyId(Long parentCompanyId);

    CustomerDto updateCustomer(CustomerDto CustomerDto, Long customerId);

    PageResponse<CustomerDto> searchCustomers(String customerName, String phone, String pincode, String personOfContact, int pageNumber);

    Boolean deleteCustomer(Long customerId, Long userId);

}