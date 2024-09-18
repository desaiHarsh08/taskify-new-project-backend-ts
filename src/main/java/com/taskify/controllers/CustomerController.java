package com.taskify.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskify.dtos.CustomerDto;
import com.taskify.services.CustomerServices;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerServices customerServices;

    @PostMapping("")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerDto customerDto) {
        CustomerDto createdCustomer = this.customerServices.createCustomer(customerDto);
        if (createdCustomer != null) {
            return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(
                this.customerServices.getCustomerByName(customerDto.getCustomerName().toUpperCase()), HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllCustomers(@RequestParam("page") int pageNumber) {
        return new ResponseEntity<>(
                this.customerServices.getAllCustomers(pageNumber), HttpStatus.OK);
    }

    @GetMapping("/filters")
    public ResponseEntity<?> getCustomersByEmailOrCityOrState(@RequestParam("page") int pageNumber,
            @RequestParam String city, @RequestParam String state, @RequestParam String email) {
        System.out.println(pageNumber + ", " + email + ", " + city + ", " + state);
        return new ResponseEntity<>(
                this.customerServices.getCustomersByEmailOrCityOrState(email, city, state, pageNumber), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCustomer(@RequestParam("page") int pageNumber,
            @RequestParam String customerName, @RequestParam String phone, @RequestParam String pincode, @RequestParam String personOfContact) {
        
        return new ResponseEntity<>(
                this.customerServices.searchCustomers(customerName, phone, pincode, personOfContact, pageNumber), HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getAllCustomers(@PathVariable String email) {
        return new ResponseEntity<>(
                this.customerServices.getCustomerByEmail(email), HttpStatus.OK);
    }

    @GetMapping("/name/{customeName}")
    public ResponseEntity<?> getCustomersByName(@PathVariable String customeName) {
        return new ResponseEntity<>(
                this.customerServices.getCustomerByName(customeName), HttpStatus.OK);
    }

    @GetMapping("/parent-company/{parentCompanyId}")
    public ResponseEntity<?> getCustomersByParentCompanyId(@PathVariable Long parentCompanyId) {
        return new ResponseEntity<>(
                this.customerServices.getCustomerByParentCompanyId(parentCompanyId), HttpStatus.OK);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long customerId) {
        return new ResponseEntity<>(
                this.customerServices.getCustomerById(customerId), HttpStatus.OK);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDto customerDto) {
        System.out.println("customerDto: " + customerDto);
        if (!customerId.equals(customerDto.getId())) {
            throw new IllegalArgumentException("Invalid customer id...");
        }

        return new ResponseEntity<>(
                this.customerServices.updateCustomer(customerDto, customerId), HttpStatus.OK);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long customerId, Long userId) {
        return new ResponseEntity<>(this.customerServices.deleteCustomer(customerId, userId), HttpStatus.OK);
    }

}
