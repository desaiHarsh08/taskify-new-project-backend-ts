package com.taskify.services;

import java.util.List;

import com.taskify.dtos.ParentCompanyDto;
import com.taskify.utils.PageResponse;

public interface ParentCompanyServices {

    ParentCompanyDto createParentCompany(ParentCompanyDto parentCompanyDto);

    PageResponse<ParentCompanyDto> getAllParentCompanies(int pageNumber);

    List<ParentCompanyDto> getAllParentCompaniesList();

    ParentCompanyDto getParentCompanyByName(String companyName);

    PageResponse<ParentCompanyDto> getParentCompaniesByState(int pageNumber, String state);

    PageResponse<ParentCompanyDto> getParentCompaniesByCity(int pageNumber, String city);

    PageResponse<ParentCompanyDto> getParentCompaniesByPincode(int pageNumber, String pincode);

    PageResponse<ParentCompanyDto> getParentCompaniesByBusinessType(int pageNumber, String businessType);

    ParentCompanyDto getParentCompanyById(Long id);

    ParentCompanyDto updateParentCompany(ParentCompanyDto parentCompanyDto);

    boolean deleteParentCompany(Long id, Long userId);

    PageResponse<ParentCompanyDto> searchParentCompanies(String companyName, String city, String state,
            String pincode, int pageNumber);

}
