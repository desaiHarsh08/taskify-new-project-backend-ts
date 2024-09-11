package com.taskify.services.impl;

import com.taskify.dtos.prototypes.FunctionPrototypeDto;
import com.taskify.dtos.prototypes.TaskPrototypeDto;
import com.taskify.exceptions.ResourceNotFoundException;
import com.taskify.models.TaskModel;
import com.taskify.models.prototypes.FunctionPrototypeModel;
import com.taskify.models.prototypes.TaskPrototypeModel;
import com.taskify.repositories.TaskRepository;
import com.taskify.repositories.prototypes.TaskPrototypeRepository;
import com.taskify.services.FunctionPrototypeServices;
import com.taskify.services.TaskPrototypeServices;
import com.taskify.services.TaskServices;
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
public class TaskPrototypeServicesImpl implements TaskPrototypeServices {

    private static final int PAGE_SIZE = 25;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TaskPrototypeRepository taskPrototypeRepository;

    @Autowired
    private FunctionPrototypeServices functionPrototypeServices;

    @Autowired
    private TaskServices taskServices;

    @Autowired
    private TaskRepository taskRepository;

    @Transactional
    @Override
    public TaskPrototypeDto createTaskPrototype(TaskPrototypeDto taskPrototypeDto) {
        // Check if the task_prototype already exists
        TaskPrototypeModel existingTaskPrototype = this.taskPrototypeRepository
                .findByTaskType(taskPrototypeDto.getTaskType().toUpperCase()).orElse(null);
        if (existingTaskPrototype != null) {
            return null; // Already exists
        }

        // System.out.println("function-prototypes: " + taskPrototypeDto.getFunctionPrototypes().size());
        // System.out.println("function-prototypes: " + taskPrototypeDto.getFunctionPrototypes());

        // Create and save the FunctionPrototypes first
        Set<FunctionPrototypeModel> functionPrototypeModels = new HashSet<>();
        for (FunctionPrototypeDto functionPrototypeDto : taskPrototypeDto.getFunctionPrototypes()) {
            functionPrototypeDto = this.functionPrototypeServices.createFunctionPrototype(functionPrototypeDto);

            FunctionPrototypeModel functionPrototypeModel = this.modelMapper.map(functionPrototypeDto,
                    FunctionPrototypeModel.class);

            functionPrototypeModels.add(functionPrototypeModel);
        }

        // System.out.println("Saving task...");
        // Create and save the TaskPrototype with its FunctionPrototypes
        TaskPrototypeModel taskPrototypeModel = this.modelMapper.map(taskPrototypeDto, TaskPrototypeModel.class);
        taskPrototypeModel.setFunctionPrototypes(functionPrototypeModels);
        TaskPrototypeModel savedTaskPrototype = this.taskPrototypeRepository.save(taskPrototypeModel);
        // System.out.println("Saved task-prototype: " + savedTaskPrototype + " \n");

        return this.taskPrototypeModelToDto(savedTaskPrototype);
    }

    @Override
    public TaskPrototypeDto addFunctionPrototypeToTaskPrototype(Long taskPrototypeId,
            FunctionPrototypeDto newFunctionPrototypeDto) {
        // Fetch the existing TaskPrototype
        TaskPrototypeModel taskPrototypeModel = this.taskPrototypeRepository.findById(taskPrototypeId).orElse(null);
        if (taskPrototypeModel == null) {
            throw new ResourceNotFoundException("TaskPrototype not found with ID: " + taskPrototypeId);
        }

        // Create the new FunctionPrototype and map it to the model
        FunctionPrototypeDto createdFunctionPrototypeDto = this.functionPrototypeServices
                .createFunctionPrototype(newFunctionPrototypeDto);
        if (createdFunctionPrototypeDto != null) {
            FunctionPrototypeModel functionPrototypeModel = this.modelMapper.map(createdFunctionPrototypeDto,
                    FunctionPrototypeModel.class);

            // Add the new FunctionPrototype to the existing TaskPrototype
            taskPrototypeModel.getFunctionPrototypes().add(functionPrototypeModel);

            // Save the updated TaskPrototype
            TaskPrototypeModel updatedTaskPrototype = this.taskPrototypeRepository.save(taskPrototypeModel);

            // Return the updated TaskPrototype as DTO
            return this.taskPrototypeModelToDto(updatedTaskPrototype);
        }

        return null; // Handle the case where the FunctionPrototype could not be created
    }

    @Override
    public PageResponse<TaskPrototypeDto> getAllTaskPrototypes(int pageNumber) {
        if (pageNumber <= 0) {
            throw new IllegalArgumentException("Page number should always be greater than 0");
        }
        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE);
        Page<TaskPrototypeModel> pageTaskPrototype = this.taskPrototypeRepository.findAll(pageable);

        List<TaskPrototypeModel> taskPrototypesList = pageTaskPrototype.getContent();

        // System.out.println(taskPrototypesList);

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTaskPrototype.getTotalPages(),
                pageTaskPrototype.getTotalElements(),
                this.taskPrototypeModelsToDtos(taskPrototypesList));
        // return null;
    }

    @Override
    public TaskPrototypeDto getTaskPrototypeById(Long id) {
        TaskPrototypeModel foundTaskPrototype = this.taskPrototypeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No task_prototype exist for id:" + id));

        // System.out.println("foundTaskPrototype: " + foundTaskPrototype);

        return this.taskPrototypeModelToDto(foundTaskPrototype);
    }

    @Override
    public TaskPrototypeDto updateTaskPrototype(TaskPrototypeDto givenTaskPrototype, Long userId) {
        TaskPrototypeModel foundTaskPrototype = this.taskPrototypeRepository.findById(givenTaskPrototype.getId())
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "No task_prototype exist for id:" + givenTaskPrototype.getId()));

        // Update the data
        if (this.taskPrototypeRepository.findByTaskType(givenTaskPrototype.getTaskType()).isPresent()) {
            throw new IllegalArgumentException("Please give the valid task_type, as it should be unique...");
        }

        foundTaskPrototype.setTaskType(givenTaskPrototype.getTaskType());

        // Save the changes
        this.taskPrototypeRepository.save(foundTaskPrototype);

        // Update the function_prototype
        for (FunctionPrototypeDto functionPrototypeDto : givenTaskPrototype.getFunctionPrototypes()) {
            this.functionPrototypeServices.updateFunctionPrototype(functionPrototypeDto);
        }

        return this.getTaskPrototypeById(givenTaskPrototype.getId());
    }

//    @Override
//    public boolean deleteTaskPrototype(Long id) {
//        // Check if the task_prototype exist
//        TaskPrototypeDto foundTaskPrototypeDto = this.getTaskPrototypeById(id);
//        // Delete all the associated task
//        TaskPrototypeModel tmpTaskPrototype = new TaskPrototypeModel();
//        tmpTaskPrototype.setId(foundTaskPrototypeDto.getId());
//        List<TaskModel> taskModels = this.taskRepository.findByTaskPrototype(tmpTaskPrototype);
//        for (TaskModel taskModel : taskModels) {
//            this.taskServices.deleteTask(taskModel.getId());
//        }
//
//        // Delete all the function_prototypes
//        for (FunctionPrototypeDto functionPrototypeDto : foundTaskPrototypeDto.getFunctionPrototypes()) {
//            this.functionPrototypeServices.deleteFunctionPrototype(functionPrototypeDto.getId());
//        }
//
//        // Delete the task_prototype
//        this.taskPrototypeRepository.deleteById(id);
//        return true;
//    }


    @Override
    public boolean deleteTaskPrototype(Long id, Long userId) {
        // Check if the task_prototype exists
        TaskPrototypeDto foundTaskPrototypeDto = this.getTaskPrototypeById(id);
        TaskPrototypeModel tmpTaskPrototype = this.modelMapper.map(foundTaskPrototypeDto, TaskPrototypeModel.class);

        // Delete all associated tasks
        List<TaskModel> taskModels = this.taskRepository.findByTaskPrototype(tmpTaskPrototype);
        for (TaskModel taskModel : taskModels) {
            this.taskServices.deleteTask(taskModel.getId(), userId);
        }

        // Delete all function prototypes associated with this task prototype
        Set<FunctionPrototypeDto> functionPrototypes = foundTaskPrototypeDto.getFunctionPrototypes();
        for (FunctionPrototypeDto functionPrototypeDto : functionPrototypes) {
            this.functionPrototypeServices.deleteFunctionPrototype(functionPrototypeDto.getId());
        }

        // Delete the task prototype
        this.taskPrototypeRepository.deleteById(id);

        return true; // Success!
    }


    private TaskPrototypeDto taskPrototypeModelToDto(TaskPrototypeModel taskPrototypeModel) {
        // System.out.println("in model -> dto, taskPrototypeModel: " + taskPrototypeModel);
        // Return, if task_prototype is null
        if (taskPrototypeModel == null) {
            return null;
        }
        TaskPrototypeDto taskPrototypeDto = this.modelMapper.map(taskPrototypeModel, TaskPrototypeDto.class);
        // Set the task_prototype_id
        taskPrototypeDto.setId(taskPrototypeModel.getId());
        // Set the function_prototypes

        // Convert function prototypes
        Set<FunctionPrototypeDto> functionPrototypeDtos = new HashSet<>();
        for (FunctionPrototypeModel functionPrototypeModel : taskPrototypeModel.getFunctionPrototypes()) {
            functionPrototypeDtos
                    .add(this.functionPrototypeServices.getFunctionPrototypeById(functionPrototypeModel.getId()));
        }
        taskPrototypeDto.setFunctionPrototypes(functionPrototypeDtos);
        // System.out.println("in model -> dto, taskPrototypeDto: " + taskPrototypeDto);
        return taskPrototypeDto;
    }

    private List<TaskPrototypeDto> taskPrototypeModelsToDtos(List<TaskPrototypeModel> taskPrototypeModels) {
        if (taskPrototypeModels == null || taskPrototypeModels.isEmpty()) {
            return new ArrayList<>();
        }

        List<TaskPrototypeDto> taskPrototypeDtos = new ArrayList<>();

        for (TaskPrototypeModel taskPrototypeModel : taskPrototypeModels) {
            taskPrototypeDtos.add(this.taskPrototypeModelToDto(taskPrototypeModel));
        }

        return taskPrototypeDtos;
    }

}
