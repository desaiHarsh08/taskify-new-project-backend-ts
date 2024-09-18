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

import com.taskify.dtos.ParentCompanyDto;
import com.taskify.services.ParentCompanyServices;

@RestController
@RequestMapping("/api/parent-companies")
public class ParentCompanyController {

    @Autowired
    private ParentCompanyServices parentCompanyServices;

    @PostMapping("")
    public ResponseEntity<?> createParentCompany(@RequestBody ParentCompanyDto parentCompanyDto) {
        return new ResponseEntity<>(this.parentCompanyServices.createParentCompany(parentCompanyDto),
                HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllParentCompanies(@RequestParam("page") int pageNumber) {
        return new ResponseEntity<>(this.parentCompanyServices.getAllParentCompanies(pageNumber), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllParentCompaniesList() {
        return new ResponseEntity<>(this.parentCompanyServices.getAllParentCompaniesList(), HttpStatus.OK);
    }

    @GetMapping("/company/{companyName}")
    public ResponseEntity<?> getParentCompanyByName(@PathVariable String companyName) {
        return new ResponseEntity<>(this.parentCompanyServices.getParentCompanyByName(companyName), HttpStatus.OK);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<?> getParentCompanyById(@PathVariable Long companyId) {
        return new ResponseEntity<>(this.parentCompanyServices.getParentCompanyById(companyId), HttpStatus.OK);
    }

    @GetMapping("/state")
    public ResponseEntity<?> getParentCompanyByState(@RequestParam("page") int pageNumber, @RequestParam String state) {
        return new ResponseEntity<>(this.parentCompanyServices.getParentCompaniesByState(pageNumber, state),
                HttpStatus.OK);
    }

    @GetMapping("/city")
    public ResponseEntity<?> getParentCompanyByCity(@RequestParam("page") int pageNumber, @RequestParam String city) {
        return new ResponseEntity<>(this.parentCompanyServices.getParentCompaniesByCity(pageNumber, city),
                HttpStatus.OK);
    }

    @GetMapping("/pincode")
    public ResponseEntity<?> getParentCompanyByPincode(@RequestParam("page") int pageNumber,
            @RequestParam String pincode) {
        return new ResponseEntity<>(this.parentCompanyServices.getParentCompaniesByPincode(pageNumber, pincode),
                HttpStatus.OK);
    }

    @GetMapping("/filters")
    public ResponseEntity<?> searchParentCompanies(@RequestParam("page") int pageNumber,
            @RequestParam String pincode,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String companyName
        ) {
        return new ResponseEntity<>(this.parentCompanyServices.searchParentCompanies(companyName, city, state, pincode, pageNumber),
                HttpStatus.OK);
    }

    @GetMapping("/business-type")
    public ResponseEntity<?> getParentCompanyByBusinessType(@RequestParam("page") int pageNumber,
            @RequestParam String businessType) {
        return new ResponseEntity<>(
                this.parentCompanyServices.getParentCompaniesByBusinessType(pageNumber, businessType),
                HttpStatus.OK);
    }

    @PutMapping("/{parentCompanyId}")
    public ResponseEntity<?> updateParentCompany(@RequestBody ParentCompanyDto parentCompanyDto,
            @PathVariable Long parentCompanyId) {
        if (!parentCompanyId.equals(parentCompanyDto.getId())) {
            throw new IllegalArgumentException("Invalid id...");
        }

        return new ResponseEntity<>(this.parentCompanyServices.updateParentCompany(parentCompanyDto),
                HttpStatus.OK);
    }

    @DeleteMapping("/{parentCompanyId}")
    public ResponseEntity<?> deleteParentCompany(@PathVariable Long parentCompanyId, @RequestParam Long userId) {
        return new ResponseEntity<>(this.parentCompanyServices.deleteParentCompany(parentCompanyId, userId),
                HttpStatus.OK);
    }

}
