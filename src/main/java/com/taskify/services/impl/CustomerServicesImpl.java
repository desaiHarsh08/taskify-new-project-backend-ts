package com.taskify.services.impl;

import com.taskify.constants.ActionType;
import com.taskify.constants.ResourceType;
import com.taskify.dtos.CustomerDto;
import com.taskify.dtos.TaskDto;
import com.taskify.exceptions.ResourceNotFoundException;
import com.taskify.models.CustomerModel;
import com.taskify.models.ParentCompanyModel;
import com.taskify.models.TaskifyTimelineModel;
import com.taskify.models.UserModel;
import com.taskify.repositories.CustomerRepository;
import com.taskify.repositories.ParentCompanyRepository;
import com.taskify.repositories.TaskifyTimelineRepository;
import com.taskify.repositories.UserRepository;
import com.taskify.services.CustomerServices;
import com.taskify.services.TaskServices;
import com.taskify.utils.PageResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServicesImpl implements CustomerServices {

    public static final int PAGE_SIZE = 1000;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TaskServices taskServices;

    @Autowired
    private TaskifyTimelineRepository taskifyTimelineRepository;

    @Autowired
    private ParentCompanyRepository parentCompanyRepository;

    @Override
    public CustomerDto createCustomer(CustomerDto customer) {
        // Check for customer already exist
        // CustomerModel existingCustomer = this.customerRepository
        // .findByCustomerNameOrEmail(customer.getCustomerName().toUpperCase(),
        // customer.getEmail().toLowerCase())
        // .orElse(null);
        // if (existingCustomer != null) {
        // return null;
        // }

        CustomerModel customerModel = this.modelMapper.map(customer, CustomerModel.class);

        if (customer.getParentCompanyId() != null) {
            ParentCompanyModel foundParentCompany = this.parentCompanyRepository.findById(customer.getParentCompanyId())
                    .orElseThrow(
                            () -> new IllegalArgumentException(
                                    "No parent_company exist for id: " + customer.getParentCompanyId()));
            customerModel.setParentCompany(foundParentCompany);
        }
        customerModel.setCustomerName(customerModel.getCustomerName().toUpperCase());

        customerModel.setBirthDate(customer.getBirthDate());
        customerModel.setAnniversary(customer.getAnniversary());

        CustomerModel savedCustomerModel = this.customerRepository.save(customerModel);

        return this.customerModelToDto(savedCustomerModel);
    }

    @Override
    public PageResponse<CustomerDto> getAllCustomers(int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number should always be greater than 0");
        }
        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE);
        Page<CustomerModel> pageCustomer = this.customerRepository.findAll(pageable);

        List<CustomerModel> customerModels = pageCustomer.getContent();

        System.out.println(customerModels);

        List<CustomerDto> customerDtos = pageCustomer.stream()
                .map(this::customerModelToDto)
                .collect(Collectors.toList());

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageCustomer.getTotalPages(),
                pageCustomer.getTotalElements(),
                this.customerModelListToDto(customerModels));
    }

    @Override
    public CustomerDto getCustomerByEmail(String email) {
        return this.customerModelToDto(
                this.customerRepository.findByEmail(email).orElseThrow(
                        () -> new ResourceNotFoundException("No customer exist with email:" + email)));
    }

    @Override
    public CustomerDto getCustomerByName(String customerName) {
        return this.customerModelToDto(
                this.customerRepository.findByCustomerName(customerName).orElseThrow(
                        () -> new ResourceNotFoundException("No customer exist with name:" + customerName)));
    }

    @Override
    public List<CustomerDto> getCustomerByParentCompanyId(Long parentCompanyId) {
        ParentCompanyModel parentCompanyModel = this.parentCompanyRepository.findById(parentCompanyId).orElseThrow(
                () -> new IllegalArgumentException("No parent_company exist for id: " + parentCompanyId));
        List<CustomerModel> customerModels = this.customerRepository.findByParentCompany(parentCompanyModel);

        return this.customerModelListToDto(customerModels);
    }

    @Override
    public CustomerDto getCustomerById(Long customerId) {
        return this.customerModelToDto(
                this.customerRepository.findById(customerId).orElseThrow(
                        () -> new ResourceNotFoundException("No customer exist with id:" + customerId)));
    }

    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto, Long customerId) {
        System.out.println("\nResidenceAddress: " + customerDto.getResidenceAddress());
        CustomerModel foundCustomer = this.customerRepository.findById(customerId).orElse(null);
        if (foundCustomer == null) {
            throw new ResourceNotFoundException("No customer exist with id:" + customerId);
        }
        foundCustomer.setCustomerName(customerDto.getCustomerName());
        foundCustomer.setPersonOfContact(customerDto.getPersonOfContact());
        foundCustomer.setPhone(customerDto.getPhone());
        foundCustomer.setCity(customerDto.getCity());
        foundCustomer.setAddress(customerDto.getAddress());
        foundCustomer.setPincode(customerDto.getPincode());
        foundCustomer.setBirthDate(customerDto.getBirthDate());
        foundCustomer.setAnniversary(customerDto.getAnniversary());
        foundCustomer.setResidenceAddress(customerDto.getResidenceAddress());
        
        CustomerModel savedCustomer = this.customerRepository.save(foundCustomer);

        return this.customerModelToDto(savedCustomer);
    }

    @Override
    public Boolean deleteCustomer(Long customerId, Long userId) {
        CustomerDto customerDto = this.getCustomerById(customerId);

        UserModel foundUser = this.userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + userId));

        // Delete all the task
        for (TaskDto taskDto : customerDto.getAllTask()) {
            this.taskServices.deleteTask(taskDto.getId(), userId);
        }
        this.customerRepository.deleteById(customerId);

        // Log the action
        TaskifyTimelineModel taskifyTimelineModel = new TaskifyTimelineModel(
                null,
                foundUser,
                ResourceType.CUSTOMER.name(),
                ActionType.DELETE.name(),
                LocalDateTime.now(),
                null,
                null,
                null,
                customerDto.getCustomerName());
        this.taskifyTimelineRepository.save(taskifyTimelineModel);

        return true;
    }

    @Override
    public PageResponse<CustomerDto> getCustomersByEmailOrCityOrState(String email, String city, String state,
            int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number should always be greater than 0");
        }

        if (email.isEmpty()) {
            email = null;
        }
        if (state.isEmpty()) {
            state = null;
        }
        if (city.isEmpty()) {
            city = null;
        }
        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE);

        System.out.println(email);
        System.out.println(city);
        System.out.println(state);

        // Fetch the customers using the repository
        Page<CustomerModel> pageCustomer = this.customerRepository.findByEmailCityOrState(email, city, state, pageable);

        // Apply pagination to the fetched customers list
        List<CustomerModel> customers = pageCustomer.getContent();

        // Build and return the PageResponse
        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageCustomer.getTotalPages(),
                pageCustomer.getTotalElements(),
                this.customerModelListToDto(customers));
    }

    public CustomerDto customerModelToDto(CustomerModel customerModel) {
        if (customerModel == null) {
            return null;
        }

        CustomerDto customerDto = this.modelMapper.map(customerModel, CustomerDto.class);

        customerDto.setAllTask(this.taskServices.getTasksByCustomer(customerModel.getId()));

        return customerDto;
    }

    public List<CustomerDto> customerModelListToDto(List<CustomerModel> customerModelList) {
        List<CustomerDto> customerDtoList = new ArrayList<>();

        for (CustomerModel customerModel : customerModelList) {
            customerDtoList.add(this.customerModelToDto(customerModel));
        }

        return customerDtoList;
    }

    @Override
    public PageResponse<CustomerDto> searchCustomers(String customerName, String phone, String pincode, String personOfContact, int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should be always greater than 0.");
        }
        System.out.println(customerName);
        System.out.println(phone);
        System.out.println(pincode);
        System.out.println(personOfContact);
        System.out.println(pageNumber);

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.Direction.DESC, "id");

        Page<CustomerModel> pageCustomer = customerRepository.findByCustomerNamePhonePincodePersonOfContact(
            customerName == null || customerName.trim().isEmpty() ? null : customerName.trim(),
            phone == null || phone.trim().isEmpty() ? null : phone.trim(),
            pincode == null || pincode.trim().isEmpty() ? null : pincode.trim(),
            personOfContact == null || personOfContact.trim().isEmpty() ? null : personOfContact.trim(),
            pageable
    );

    List<CustomerModel> customerModels = pageCustomer.getContent();

        return new PageResponse(
            pageNumber,
            PAGE_SIZE,
            pageCustomer.getTotalPages(),
            pageCustomer.getTotalElements(),
            this.customerModelListToDto(customerModels)
        );
    }

}
