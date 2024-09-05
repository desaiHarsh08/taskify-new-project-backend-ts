package com.taskify.services.impl;

import com.taskify.dtos.prototypes.FieldPrototypeDto;
import com.taskify.dtos.prototypes.FunctionPrototypeDto;
import com.taskify.exceptions.ResourceNotFoundException;
import com.taskify.models.prototypes.FieldPrototypeModel;
import com.taskify.models.prototypes.FunctionFieldPrototypeModel;
import com.taskify.models.prototypes.FunctionPrototypeModel;
import com.taskify.models.prototypes.TaskPrototypeModel;
import com.taskify.repositories.prototypes.FunctionFieldPrototypeRepository;
import com.taskify.repositories.prototypes.FunctionPrototypeRepository;
import com.taskify.repositories.prototypes.TaskPrototypeRepository;
import com.taskify.services.FieldPrototypeServices;
import com.taskify.services.FunctionPrototypeServices;
import com.taskify.utils.PageResponse;

import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FunctionPrototypeServicesImpl implements FunctionPrototypeServices {

    private static final int PAGE_SIZE = 25;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FunctionPrototypeRepository functionPrototypeRepository;

    @Autowired
    private FieldPrototypeServices fieldPrototypeServices;

    @Autowired
    private FunctionFieldPrototypeRepository functionFieldPrototypeRepository;

    @Autowired
    private TaskPrototypeRepository taskPrototypeRepository;

    @Transactional
    @Override
    public FunctionPrototypeDto createFunctionPrototype(FunctionPrototypeDto functionPrototypeDto) {
        FunctionPrototypeModel foundFunctionPrototype = this.functionPrototypeRepository
                .findByTitle(functionPrototypeDto.getTitle()).orElse(null);

        System.out.println("foundFunctionPrototype: " + foundFunctionPrototype);

        if (foundFunctionPrototype != null) { // Already exist
            return this.functionPrototypeModelToDto(foundFunctionPrototype);
        }

        // Create the function_prototype
        FunctionPrototypeModel functionPrototypeModel = this.modelMapper.map(functionPrototypeDto,
                FunctionPrototypeModel.class);
        System.out.println("Saving fn-prototype...");
        FunctionPrototypeModel savedFunctionPrototype = this.functionPrototypeRepository.save(functionPrototypeModel);
        System.out.println("fn-prototype saved!, " + savedFunctionPrototype);

        // Create the fields
        for (FieldPrototypeDto fieldPrototypeDto : functionPrototypeDto.getFieldPrototypes()) {
            System.out.println("\ngoing to create a field...");
            FieldPrototypeDto savedFieldPrototypeDto = this.fieldPrototypeServices
                    .createFieldPrototype(fieldPrototypeDto);
            // Convert the dto to model
            FieldPrototypeModel fieldPrototypeModel = this.modelMapper.map(savedFieldPrototypeDto,
                    FieldPrototypeModel.class);

            // Create the function_field_prototype
            FunctionFieldPrototypeModel foundFunctionFieldPrototype = this.functionFieldPrototypeRepository
                    .findByFunctionPrototypeAndFieldPrototype(savedFunctionPrototype, fieldPrototypeModel).orElse(null);
            if (foundFunctionFieldPrototype != null) {
                continue;
            }
            FunctionFieldPrototypeModel functionFieldPrototypeModel = new FunctionFieldPrototypeModel(null,
                    savedFunctionPrototype, fieldPrototypeModel);
            this.functionFieldPrototypeRepository.save(functionFieldPrototypeModel);
        }

        // Check for the combined_task
        TaskPrototypeModel combinedTaskPrototypeModel = this.taskPrototypeRepository.findByTaskType("COMBINED_TASK")
                .orElse(null);
        if (combinedTaskPrototypeModel != null) {
            if (!combinedTaskPrototypeModel.getFunctionPrototypes().contains(savedFunctionPrototype)) {
                combinedTaskPrototypeModel.getFunctionPrototypes().add(savedFunctionPrototype);
                this.taskPrototypeRepository.save(combinedTaskPrototypeModel);
            }
        }

        return this.functionPrototypeModelToDto(functionPrototypeModel);
    }

    @Override
    public PageResponse<FunctionPrototypeDto> getAllFunctionPrototypes(int pageNumber) {
        if (pageNumber <= 0) {
            throw new IllegalArgumentException("Page number should always be greater than 0");
        }
        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE);

        Page<FunctionPrototypeModel> pageFunctionPrototype = this.functionPrototypeRepository.findAll(pageable);

        List<FunctionPrototypeModel> functionPrototypeList = pageFunctionPrototype.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageFunctionPrototype.getTotalPages(),
                pageFunctionPrototype.getTotalElements(),
                this.functionPrototypeModelListToDtos(functionPrototypeList));
    }

    @Override
    public List<FunctionPrototypeDto> getFunctionPrototypesByDepartment(String department) {
        List<FunctionPrototypeModel> functionPrototypeModels = this.functionPrototypeRepository
                .findByDepartment(department);

        return this.functionPrototypeModelListToDtos(functionPrototypeModels);
    }

    @Override
    public FunctionPrototypeDto getFunctionPrototypeById(Long id) {
        FunctionPrototypeModel functionPrototypeModel = this.functionPrototypeRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("No function_prototype exist for id: " + id));

        return this.functionPrototypeModelToDto(functionPrototypeModel);
    }

    @Override
    public FunctionPrototypeDto getFunctionPrototypeByTitle(String title) {
        FunctionPrototypeModel functionPrototypeModel = this.functionPrototypeRepository.findByTitle(title).orElseThrow(
                () -> new ResourceNotFoundException("No function_prototype exist for title: " + title));

        return this.functionPrototypeModelToDto(functionPrototypeModel);
    }

    @Override
    public FunctionPrototypeDto updateFunctionPrototype(FunctionPrototypeDto givenFunctionPrototypeDto) {
        FunctionPrototypeModel functionPrototypeModel = this.functionPrototypeRepository
                .findById(givenFunctionPrototypeDto.getId()).orElseThrow(
                        () -> new ResourceNotFoundException(
                                "No function_prototype exist for id: " + givenFunctionPrototypeDto.getId()));

        // Update the function_prototype data
        functionPrototypeModel.setTitle(givenFunctionPrototypeDto.getTitle());
        functionPrototypeModel.setDescription(givenFunctionPrototypeDto.getDescription());
        functionPrototypeModel.setDepartment(givenFunctionPrototypeDto.getDepartment());
        functionPrototypeModel.setChoice(givenFunctionPrototypeDto.isChoice());

        // Save the function_prototype
        functionPrototypeModel = this.functionPrototypeRepository.save(functionPrototypeModel);

        // Update the fields
        for (FieldPrototypeDto fieldPrototypeDto : givenFunctionPrototypeDto.getFieldPrototypes()) {
            this.fieldPrototypeServices.updateFieldPrototype(fieldPrototypeDto);
        }

        return this.getFunctionPrototypeById(functionPrototypeModel.getId());
    }

    // @Override
    // public boolean deleteFunctionPrototype(Long id) {
    // // Check for the function_prototype exist
    // FunctionPrototypeDto foundFunctionPrototypeDto =
    // this.getFunctionPrototypeById(id);
    //
    // FunctionPrototypeModel functionPrototypeModel =
    // this.modelMapper.map(foundFunctionPrototypeDto,
    // FunctionPrototypeModel.class);
    //
    // List<FunctionFieldPrototypeModel> functionFieldPrototypeModels =
    // this.functionFieldPrototypeRepository.findByFunctionPrototype(functionPrototypeModel);
    //
    // for (FunctionFieldPrototypeModel functionFieldPrototypeModel:
    // functionFieldPrototypeModels) {
    // // Delete the function_field_prototype
    // this.functionFieldPrototypeRepository.deleteById(functionFieldPrototypeModel.getId());
    // // Delete the field_prototype
    // this.fieldPrototypeServices.deleteFieldPrototype(functionFieldPrototypeModel.getFieldPrototype().getId());
    // }
    //
    // // Remove the function prototype from all associated task prototypes
    // for (TaskPrototypeModel taskPrototype :
    // functionPrototypeModel.getTaskPrototypes()) {
    // taskPrototype.getFunctionPrototypes().remove(functionPrototypeModel);
    // }
    //
    // // Clear all function prototypes associated with this function prototype
    // functionPrototypeModel.getTaskPrototypes().clear();
    //
    // // Delete the function_prototype
    // this.functionPrototypeRepository.deleteById(id);
    //
    // return true; // Success!
    // }

    @Override
    public boolean deleteFunctionPrototype(Long id) {
        // Check if the function_prototype exists
        FunctionPrototypeDto foundFunctionPrototypeDto = this.getFunctionPrototypeById(id);
        FunctionPrototypeModel functionPrototypeModel = this.modelMapper.map(foundFunctionPrototypeDto,
                FunctionPrototypeModel.class);

        // Delete associated function field prototypes
        List<FunctionFieldPrototypeModel> functionFieldPrototypeModels = this.functionFieldPrototypeRepository
                .findByFunctionPrototype(functionPrototypeModel);
        for (FunctionFieldPrototypeModel functionFieldPrototypeModel : functionFieldPrototypeModels) {
            // Delete the function field prototype
            this.functionFieldPrototypeRepository.deleteById(functionFieldPrototypeModel.getId());
            // Delete the field prototype
            this.fieldPrototypeServices.deleteFieldPrototype(functionFieldPrototypeModel.getFieldPrototype().getId());
        }

        // Remove the function prototype from all associated task prototypes
        Set<TaskPrototypeModel> taskPrototypes = functionPrototypeModel.getTaskPrototypes();
        for (TaskPrototypeModel taskPrototype : taskPrototypes) {
            taskPrototype.getFunctionPrototypes().remove(functionPrototypeModel);
            // Save changes to update the association in the database
            this.taskPrototypeRepository.save(taskPrototype);
        }

        // Clear all function prototypes associated with this function prototype
        functionPrototypeModel.getTaskPrototypes().clear();

        // Delete the function prototype
        this.functionPrototypeRepository.deleteById(id);

        return true; // Success!
    }

    @Override
    public FunctionPrototypeDto functionPrototypeModelToDto(FunctionPrototypeModel functionPrototypeModel) {
        if (functionPrototypeModel == null) {
            return null;
        }

        FunctionPrototypeDto functionPrototypeDto = this.modelMapper.map(functionPrototypeModel,
                FunctionPrototypeDto.class);

        functionPrototypeDto.setId(functionPrototypeModel.getId());

        // Fetch all the field_prototypes
        List<FunctionFieldPrototypeModel> functionFieldPrototypeModels = this.functionFieldPrototypeRepository
                .findByFunctionPrototype(functionPrototypeModel);
        for (FunctionFieldPrototypeModel functionFieldPrototypeModel : functionFieldPrototypeModels) {
            FieldPrototypeDto fieldPrototypeDto = this.fieldPrototypeServices
                    .getFieldPrototypeById(functionFieldPrototypeModel.getFieldPrototype().getId());
            if (fieldPrototypeDto != null) {
                functionPrototypeDto.getFieldPrototypes().add(fieldPrototypeDto);
            }
        }

        return functionPrototypeDto;
    }

    @Override
    public Set<FunctionPrototypeDto> functionPrototypeModelsToDtos(
            Set<FunctionPrototypeModel> functionPrototypeModels) {
        if (functionPrototypeModels == null || functionPrototypeModels.isEmpty()) {
            return new HashSet<>();
        }

        Set<FunctionPrototypeDto> functionPrototypeDtos = new HashSet<>();

        for (FunctionPrototypeModel functionPrototypeModel : functionPrototypeModels) {
            functionPrototypeDtos.add(this.functionPrototypeModelToDto(functionPrototypeModel));
        }

        return functionPrototypeDtos;
    }

    public List<FunctionPrototypeDto> functionPrototypeModelListToDtos(
            List<FunctionPrototypeModel> functionPrototypeModels) {
        if (functionPrototypeModels == null || functionPrototypeModels.isEmpty()) {
            return new ArrayList<>();
        }

        List<FunctionPrototypeDto> functionPrototypeDtos = new ArrayList<>();

        for (FunctionPrototypeModel functionPrototypeModel : functionPrototypeModels) {
            functionPrototypeDtos.add(this.functionPrototypeModelToDto(functionPrototypeModel));
        }

        return functionPrototypeDtos;
    }

}
