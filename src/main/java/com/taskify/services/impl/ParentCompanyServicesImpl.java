package com.taskify.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.taskify.dtos.CustomerDto;
import com.taskify.dtos.ParentCompanyDto;
import com.taskify.exceptions.ResourceNotFoundException;
import com.taskify.models.ParentCompanyModel;
import com.taskify.models.UserModel;
import com.taskify.repositories.ParentCompanyRepository;
import com.taskify.repositories.UserRepository;
import com.taskify.services.CustomerServices;
import com.taskify.services.ParentCompanyServices;
import com.taskify.utils.PageResponse;

@Service
public class ParentCompanyServicesImpl implements ParentCompanyServices {

    private static final int PAGE_SIZE = 100;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ParentCompanyRepository parentCompanyRepository;

    @Autowired
    private CustomerServices customerServices;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ParentCompanyDto createParentCompany(ParentCompanyDto parentCompanyDto) {
        System.out.println("Given parent-company: " + parentCompanyDto + "\n\n");
        ParentCompanyModel existingCompany = this.parentCompanyRepository
                .findByCompanyName(parentCompanyDto.getCompanyName()).orElse(null);
        if (existingCompany != null) {
            throw new IllegalArgumentException("Please provide the distinct company_name");
        }

        ParentCompanyModel parentCompanyModel = this.modelMapper.map(parentCompanyDto, ParentCompanyModel.class);
        parentCompanyModel = this.parentCompanyRepository.save(parentCompanyModel);

        return this.parentCompanyModelToDto(parentCompanyModel);
    }

    @Override
    public PageResponse<ParentCompanyDto> getAllParentCompanies(int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0");
        }
        System.out.println("here");
        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<ParentCompanyModel> pageParentCompany = this.parentCompanyRepository.findAll(pageable);

        List<ParentCompanyModel> parentCompanyModels = pageParentCompany.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageParentCompany.getTotalPages(),
                pageParentCompany.getTotalElements(),
                this.parentCompanyModelListToDtoList(parentCompanyModels));
    }

    @Override
    public ParentCompanyDto getParentCompanyByName(String companyName) {
        ParentCompanyModel foundParentCompany = this.parentCompanyRepository.findByCompanyName(companyName).orElseThrow(
                () -> new ResourceNotFoundException("No parent_company exist for company_name: " + companyName));

        return this.parentCompanyModelToDto(foundParentCompany);
    }

    @Override
    public PageResponse<ParentCompanyDto> getParentCompaniesByState(int pageNumber, String state) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<ParentCompanyModel> pageParentCompany = this.parentCompanyRepository.findByState(pageable, state);

        List<ParentCompanyModel> parentCompanyModels = pageParentCompany.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageParentCompany.getTotalPages(),
                pageParentCompany.getTotalElements(),
                this.parentCompanyModelListToDtoList(parentCompanyModels));
    }

    @Override
    public PageResponse<ParentCompanyDto> getParentCompaniesByCity(int pageNumber, String city) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<ParentCompanyModel> pageParentCompany = this.parentCompanyRepository.findByCity(pageable, city);

        List<ParentCompanyModel> parentCompanyModels = pageParentCompany.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageParentCompany.getTotalPages(),
                pageParentCompany.getTotalElements(),
                this.parentCompanyModelListToDtoList(parentCompanyModels));
    }

    @Override
    public PageResponse<ParentCompanyDto> getParentCompaniesByPincode(int pageNumber, String pincode) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<ParentCompanyModel> pageParentCompany = this.parentCompanyRepository.findByPincode(pageable, pincode);

        List<ParentCompanyModel> parentCompanyModels = pageParentCompany.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageParentCompany.getTotalPages(),
                pageParentCompany.getTotalElements(),
                this.parentCompanyModelListToDtoList(parentCompanyModels));
    }

    @Override
    public PageResponse<ParentCompanyDto> getParentCompaniesByBusinessType(int pageNumber, String businessType) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<ParentCompanyModel> pageParentCompany = this.parentCompanyRepository.findByBusinessType(pageable,
                businessType);

        List<ParentCompanyModel> parentCompanyModels = pageParentCompany.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageParentCompany.getTotalPages(),
                pageParentCompany.getTotalElements(),
                this.parentCompanyModelListToDtoList(parentCompanyModels));
    }

    @Override
    public ParentCompanyDto getParentCompanyById(Long id) {
        ParentCompanyModel foundParentCompany = this.parentCompanyRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No parent_company exist for id: " + id));

        return this.parentCompanyModelToDto(foundParentCompany);
    }

    @Override
    public ParentCompanyDto updateParentCompany(ParentCompanyDto parentCompanyDto) {
        ParentCompanyModel foundParentCompany = this.parentCompanyRepository
                .findById(parentCompanyDto.getId()).orElseThrow(
                        () -> new ResourceNotFoundException(
                                "No parent_company exist for company_name: " + parentCompanyDto.getCompanyName()));

        foundParentCompany.setAddress(parentCompanyDto.getAddress());
        foundParentCompany.setBusinessType(parentCompanyDto.getBusinessType());
        foundParentCompany.setCity(parentCompanyDto.getCity());
        foundParentCompany.setHeadOfficeAddress(parentCompanyDto.getHeadOfficeAddress());
        foundParentCompany.setPersonOfContact(parentCompanyDto.getPersonOfContact());
        foundParentCompany.setPhone(parentCompanyDto.getPhone());
        foundParentCompany.setPincode(parentCompanyDto.getPincode());
        foundParentCompany.setRemarks(parentCompanyDto.getRemarks());

        this.parentCompanyRepository.save(foundParentCompany);

        return this.parentCompanyModelToDto(foundParentCompany);
    }

    @Override
    public List<ParentCompanyDto> getAllParentCompaniesList() {
        return this.parentCompanyModelListToDtoList(this.parentCompanyRepository.findAll());
    }

    @Override
    public boolean deleteParentCompany(Long id, Long userId) {
        ParentCompanyDto foundParentCompanyDto = this.getParentCompanyById(id);
        // Delete all the customers
        for (CustomerDto customerDto : foundParentCompanyDto.getCustomers()) {
            this.customerServices.deleteCustomer(customerDto.getId(), userId);
        }

        // Delete the parent_company
        this.parentCompanyRepository.deleteById(foundParentCompanyDto.getId());

        return true; // Success!
    }

    private ParentCompanyDto parentCompanyModelToDto(ParentCompanyModel parentCompanyModel) {
        if (parentCompanyModel == null) {
            return null;
        }
        ParentCompanyDto parentCompanyDto = this.modelMapper.map(parentCompanyModel, ParentCompanyDto.class);
        parentCompanyDto.setId(parentCompanyModel.getId());
        parentCompanyDto.setCustomers(this.customerServices.getCustomerByParentCompanyId(parentCompanyModel.getId()));

        return parentCompanyDto;
    }

    private List<ParentCompanyDto> parentCompanyModelListToDtoList(List<ParentCompanyModel> parentCompanyModelList) {
        if (parentCompanyModelList == null || parentCompanyModelList.isEmpty()) {
            return new ArrayList<>();
        }

        List<ParentCompanyDto> parentCompanyDtos = new ArrayList<>();
        for (ParentCompanyModel parentCompanyModel : parentCompanyModelList) {
            parentCompanyDtos.add(this.parentCompanyModelToDto(parentCompanyModel));
        }

        return parentCompanyDtos;
    }

    @Override
    public PageResponse<ParentCompanyDto> searchParentCompanies(String companyName, String city, String state,
            String pincode, int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number should be greater than 0.");
        }

        System.out.println(companyName);
        System.out.println(city);
        System.out.println(state);
        System.out.println(pincode);
        System.out.println(pageNumber);

        // Pageable configuration with sorting by "id" and descending order
        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.Direction.DESC, "id");

        // Query the repository with null checks and trimmed values
        Page<ParentCompanyModel> pageParentCompanies = parentCompanyRepository.findByCompanyNameCityStatePincode(
                companyName == null || companyName.trim().isEmpty() ? null : companyName.trim(),
                city == null || city.trim().isEmpty() ? null : city.trim(),
                state == null || state.trim().isEmpty() ? null : state.trim(),
                pincode == null || pincode.trim().isEmpty() ? null : pincode.trim(),
                pageable);

        // Create PageResponse with pagination data
        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE, // Page number starts from 0, so increment by 1
                pageParentCompanies.getTotalPages(),
                pageParentCompanies.getTotalElements(),
                this.parentCompanyModelListToDtoList(pageParentCompanies.getContent()));
    }

}
