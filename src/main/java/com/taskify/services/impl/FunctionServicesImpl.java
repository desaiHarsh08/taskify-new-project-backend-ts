package com.taskify.services.impl;

import com.taskify.constants.ActionType;
import com.taskify.constants.Department;
import com.taskify.constants.ResourceType;
import com.taskify.dtos.FieldDto;
import com.taskify.dtos.FileDto;
import com.taskify.dtos.FunctionDto;
import com.taskify.exceptions.ResourceNotFoundException;
import com.taskify.externals.email.EmailServices;
import com.taskify.models.FunctionModel;
import com.taskify.models.TaskModel;
import com.taskify.models.TaskifyTimelineModel;
import com.taskify.models.UserModel;
import com.taskify.models.prototypes.FunctionPrototypeModel;
import com.taskify.repositories.*;
import com.taskify.repositories.prototypes.FunctionPrototypeRepository;
import com.taskify.services.FieldServices;
import com.taskify.services.FunctionServices;
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

@Service
public class FunctionServicesImpl implements FunctionServices {

    private static final int PAGE_SIZE = 25;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FunctionPrototypeRepository functionPrototypeRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private FieldServices fieldServices;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskifyTimelineRepository taskifyTimelineRepository;

    @Autowired
    private EmailServices emailServices;

    @Override
    public FunctionDto createFunction(FunctionDto functionDto) {
        System.out.println("\n\nfn: " + functionDto + "\n\n");
        FunctionModel functionModel = this.modelMapper.map(functionDto, FunctionModel.class);
        functionModel.setCreatedDate(functionDto.getCreatedDate());
        functionModel.setDueDate(functionDto.getDueDate());
        functionModel.setClosed(false);
        functionModel.setClosedByUser(null);
        functionModel.setClosedDate(null);

        // Check for the task
        TaskModel foundTask = this.taskRepository.findById(functionDto.getTaskId()).orElseThrow(
                () -> new IllegalArgumentException("No task exist for id: " + functionDto.getTaskId()));

        UserModel taskCreatedByUser = this.userRepository.findById(foundTask.getCreatedByUser().getId()).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + foundTask.getCreatedByUser().getId()));

        UserModel taskAssignedToUser = this.userRepository.findById(foundTask.getAssignedToUser().getId()).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + foundTask.getAssignedToUser().getId()));

        foundTask.setCreatedByUser(taskCreatedByUser);
        foundTask.setAssignedToUser(taskAssignedToUser);

        functionModel.setTask(foundTask);

        // Check for the function_prototype
        FunctionPrototypeModel foundFunctionPrototype = this.functionPrototypeRepository
                .findById(functionDto.getFunctionPrototypeId()).orElseThrow(
                        () -> new IllegalArgumentException(
                                "No function_prototype exist for id: " + functionDto.getFunctionPrototypeId()));
        functionModel.setFunctionPrototype(foundFunctionPrototype);

        // Check for the created_by_user
        UserModel foundCreatedByUser = this.userRepository.findById(functionDto.getCreatedByUserId()).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + functionDto.getCreatedByUserId()));
        functionModel.setCreatedByUser(foundCreatedByUser);

        functionModel.setClosedByUser(null);

        // Create the function
        Long id = (this.functionRepository.save(functionModel)).getId();
        functionModel.setId(id);

        // Create the fields
        for (FieldDto fieldDto : functionDto.getFields()) {
            fieldDto.setFunctionId(functionModel.getId());
            this.fieldServices.createField(fieldDto);
        }

        // Log the action
        TaskifyTimelineModel taskifyTimelineModel = new TaskifyTimelineModel(
                null,
                foundCreatedByUser,
                ResourceType.FUNCTION.name(),
                ActionType.CREATE.name(),
                functionModel.getCreatedDate(),
                foundTask.getTaskAbbreviation(),
                foundFunctionPrototype.getTitle(),
                null,
                null);
        this.taskifyTimelineRepository.save(taskifyTimelineModel);

        // Notify the user
        String subject = "Function Assignment on Taskify";
        String body = this.generateEmailBody(functionModel);
        this.emailServices.sendSimpleMessage(foundTask.getAssignedToUser().getEmail(), subject, body);
        return this.functionModelToDto(functionModel);
    }

    public String generateEmailBody(FunctionModel functionModel) {
        String body = "Dear " + functionModel.getTask().getAssignedToUser().getName() + ",\n\n" +
                "We hope this message finds you well.\n\n" +
                "We are pleased to inform you that a function has been assigned to you on Taskify. Below are the details of the task and function:\n\n"
                +
                "Task Type     : " + functionModel.getTask().getTaskPrototype().getTaskType() + "\n" +
                "Task Priorrity: " + functionModel.getTask().getPriorityType() + "\n" +
                "Function      : " + functionModel.getFunctionPrototype().getTitle() + "\n" +
                "Due Date      : " + functionModel.getDueDate() + "\n" +
                "Department    : " + functionModel.getFunctionPrototype().getDepartment() + "\n" +
                "Created By    : " + functionModel.getCreatedByUser().getName() + "\n\n" +
                "Please log in to your Taskify account to review the task details and manage your workflow. Should you have any questions or require further information, feel free to reach out.\n\n"
                +
                "Thank you for your attention and cooperation.\n\n" +
                "Best regards,\n\n" +
                "Taskify Software";

        return body;
    }

    @Override
    public PageResponse<FunctionDto> getAllFunctions(int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<FunctionModel> pageFunction = this.functionRepository.findAll(pageable);

        List<FunctionModel> taskModels = pageFunction.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageFunction.getTotalPages(),
                pageFunction.getTotalElements(),
                this.functionModelsToDtos(taskModels));
    }

    @Override
    public FunctionDto getFunctionById(Long id) {
        FunctionModel foundFunctionModel = this.functionRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("No function exist for id: " + id));

        return this.functionModelToDto(foundFunctionModel);
    }

    @Override
    public List<FunctionDto> getFunctionsByTaskId(Long taskId) {
        TaskModel foundTask = this.taskRepository.findById(taskId).orElseThrow(
                () -> new IllegalArgumentException("No task exist for id: " + taskId));
        List<FunctionModel> functionModels = this.functionRepository.findByTaskOrderByIdDesc(foundTask);

        return this.functionModelsToDtos(functionModels);
    }

    @Override
    public PageResponse<FunctionDto> getFunctionsByDepartment(int pageNumber, String department) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        if (!department.equals(Department.SERVICE.name()) &&
                !department.equals(Department.QUOTATION.name()) &&
                !department.equals(Department.ACCOUNTS.name()) &&
                !department.equals(Department.DISPATCH.name()) &&
                !department.equals(Department.CUSTOMER.name())) {
            throw new IllegalArgumentException("Invalid department provided!");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<FunctionModel> pageFunction = this.functionRepository.findByDepartment(pageable, department);

        List<FunctionModel> taskModels = pageFunction.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageFunction.getTotalPages(),
                pageFunction.getTotalElements(),
                this.functionModelsToDtos(taskModels));
    }

    @Override
    public PageResponse<FunctionDto> getFunctionsByCreatedByUser(int pageNumber, Long createdByUserId) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        UserModel foundCreatedByUser = this.userRepository.findById(createdByUserId).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + createdByUserId));

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<FunctionModel> pageFunction = this.functionRepository.findByCreatedByUser(pageable, foundCreatedByUser);

        List<FunctionModel> taskModels = pageFunction.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageFunction.getTotalPages(),
                pageFunction.getTotalElements(),
                this.functionModelsToDtos(taskModels));
    }

    @Override
    public PageResponse<FunctionDto> getFunctionsByCreatedDate(int pageNumber, Date createdDate) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "created_date"));

        Page<FunctionModel> pageFunction = this.functionRepository.findByCreatedDate(pageable, createdDate);

        List<FunctionModel> taskModels = pageFunction.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageFunction.getTotalPages(),
                pageFunction.getTotalElements(),
                this.functionModelsToDtos(taskModels));
    }

    @Override
    public PageResponse<FunctionDto> getFunctionsByDueDate(int pageNumber, Date dueDate) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<FunctionModel> pageFunction = this.functionRepository.findByDueDate(pageable, dueDate);

        List<FunctionModel> taskModels = pageFunction.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageFunction.getTotalPages(),
                pageFunction.getTotalElements(),
                this.functionModelsToDtos(taskModels));
    }

    @Override
    public PageResponse<FunctionDto> getFunctionsByClosedByUserId(int pageNumber, Long closedByUserId) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        UserModel foundCreatedByUser = this.userRepository.findById(closedByUserId).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + closedByUserId));

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<FunctionModel> pageFunction = this.functionRepository.findByClosedByUser(pageable, foundCreatedByUser);

        List<FunctionModel> taskModels = pageFunction.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageFunction.getTotalPages(),
                pageFunction.getTotalElements(),
                this.functionModelsToDtos(taskModels));
    }

    @Override
    public PageResponse<FunctionDto> getFunctionsByClosedDate(int pageNumber, Date closedDate) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<FunctionModel> pageFunction = this.functionRepository.findByClosedDate(pageable, closedDate);

        List<FunctionModel> taskModels = pageFunction.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageFunction.getTotalPages(),
                pageFunction.getTotalElements(),
                this.functionModelsToDtos(taskModels));
    }

    @Override
    public PageResponse<FunctionDto> getFunctionsByIsClosed(int pageNumber, boolean isClosed) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<FunctionModel> pageFunction = this.functionRepository.findByIsClosed(pageable, isClosed);

        List<FunctionModel> taskModels = pageFunction.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageFunction.getTotalPages(),
                pageFunction.getTotalElements(),
                this.functionModelsToDtos(taskModels));
    }

    @Override
    public FunctionDto updateFunction(FunctionDto givenFunctionDto, Long userId) {
        TaskModel foundTask = this.taskRepository.findById(givenFunctionDto.getTaskId()).orElseThrow(
                () -> new IllegalArgumentException("No task exist for id: " + givenFunctionDto.getTaskId()));

        FunctionModel foundFunction = this.functionRepository.findById(givenFunctionDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException("No function exist for id: " + givenFunctionDto.getId()));

        FunctionPrototypeModel foundFunctionPrototype = this.functionPrototypeRepository
                .findById(givenFunctionDto.getFunctionPrototypeId()).orElseThrow(
                        () -> new IllegalArgumentException(
                                "No function_prototype exist for id: " + givenFunctionDto.getFunctionPrototypeId()));

        // Update the data
        foundFunction.setDueDate(givenFunctionDto.getDueDate());
        foundFunction.setClosed(givenFunctionDto.getIsClosed());
        foundFunction.setClosedDate(givenFunctionDto.getClosedDate());

        UserModel foundUser = this.userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + userId));

        // Save the data
        this.functionRepository.save(foundFunction);

        // Log the action
        TaskifyTimelineModel taskifyTimelineModel = new TaskifyTimelineModel(
                null,
                foundUser,
                ResourceType.FUNCTION.name(),
                ActionType.UPDATE.name(),
                LocalDateTime.now(),
                foundTask.getTaskAbbreviation(),
                foundFunctionPrototype.getTitle(),
                null,
                null);
        this.taskifyTimelineRepository.save(taskifyTimelineModel);

        // TODO: Notify the user if function got closed and broadcast the message
        if (givenFunctionDto.getIsClosed()) {

        }

        return this.functionModelToDto(foundFunction);
    }

    @Override
    public FunctionDto closeFunction(FunctionDto givenFunctionDto, Long userId) {
        TaskModel foundTask = this.taskRepository.findById(givenFunctionDto.getTaskId()).orElseThrow(
                () -> new IllegalArgumentException("No task exist for id: " + givenFunctionDto.getTaskId()));

        FunctionPrototypeModel foundFunctionPrototype = this.functionPrototypeRepository
                .findById(givenFunctionDto.getFunctionPrototypeId()).orElseThrow(
                        () -> new IllegalArgumentException(
                                "No function_prototype exist for id: " + givenFunctionDto.getFunctionPrototypeId()));

        FunctionModel foundFunction = this.functionRepository.findById(givenFunctionDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException("No function exist for id: " + givenFunctionDto.getId()));

        // Check whether function is eligible to close
        List<FieldDto> fieldDtos = this.fieldServices.getFieldsByFunctionId(givenFunctionDto.getId());
        for (FieldDto fieldDto : fieldDtos) {
            if (!fieldDto.getIsClosed()) {
                throw new IllegalArgumentException(
                        "The given function can't be closed. Please close all the fields first inorder to close the function!");
            }
        }

        UserModel closedByUser = this.userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + givenFunctionDto.getClosedByUserId()));

        // Close the function
        foundFunction.setClosed(true);
        foundFunction.setClosedByUser(closedByUser);
        foundFunction.setClosedDate(LocalDateTime.now());

        // Save the function
        this.functionRepository.save(foundFunction);

        // Log the action
        TaskifyTimelineModel taskifyTimelineModel = new TaskifyTimelineModel(
                null,
                closedByUser,
                ResourceType.FUNCTION.name(),
                ActionType.CLOSED.name(),
                foundFunction.getClosedDate(),
                foundTask.getTaskAbbreviation(),
                foundFunctionPrototype.getTitle(),
                null,
                null);
        this.taskifyTimelineRepository.save(taskifyTimelineModel);

        // TODO: Notify the user

        return this.functionModelToDto(foundFunction);
    }

    @Override
    public boolean deleteFunction(Long id, Long userId) {
        FunctionDto foundFunction = this.getFunctionById(id);

        UserModel foundUser = this.userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + userId));

        FunctionPrototypeModel foundFunctionPrototype = this.functionPrototypeRepository
                .findById(foundFunction.getFunctionPrototypeId()).orElseThrow(
                        () -> new IllegalArgumentException(
                                "No function_prototype exist for id: " + foundFunction.getFunctionPrototypeId()));

        TaskModel foundTask = this.taskRepository.findById(foundFunction.getTaskId()).orElseThrow(
                () -> new IllegalArgumentException("No task exist for id: " + foundFunction.getTaskId()));

        // Delete the fields
        for (FieldDto fieldDto : foundFunction.getFields()) {
            this.fieldServices.deleteField(fieldDto.getId(), userId);
        }

        // Delete the functions
        this.functionRepository.deleteById(id);

        // Log the action
        TaskifyTimelineModel taskifyTimelineModel = new TaskifyTimelineModel(
                null,
                foundUser,
                ResourceType.FUNCTION.name(),
                ActionType.CLOSED.name(),
                foundFunction.getClosedDate(),
                foundTask.getTaskAbbreviation(),
                foundFunctionPrototype.getTitle(),
                null,
                null);
        this.taskifyTimelineRepository.save(taskifyTimelineModel);

        // TODO: Notify the user

        return true; // Success!
    }

    private FunctionDto functionModelToDto(FunctionModel functionModel) {
        if (functionModel == null) {
            return null;
        }

        FunctionDto functionDto = this.modelMapper.map(functionModel, FunctionDto.class);
        functionDto.setFunctionPrototypeId(functionModel.getFunctionPrototype().getId());
        functionDto.setTaskId(functionModel.getTask().getId());
        functionDto.setIsClosed(functionModel.isClosed());
        functionDto.setCreatedByUserId(functionModel.getCreatedByUser().getId());
        if (functionModel.getClosedByUser() != null) {
            functionDto.setClosedByUserId(functionModel.getClosedByUser().getId());
        }

        functionDto.setFields(this.fieldServices.getFieldsByFunctionId(functionModel.getId()));

        return functionDto;
    }

    private List<FunctionDto> functionModelsToDtos(List<FunctionModel> functionModels) {
        List<FunctionDto> functionDtos = new ArrayList<>();

        if (functionModels != null) {
            for (FunctionModel functionModel : functionModels) {
                functionDtos.add(this.functionModelToDto(functionModel));
            }
        }

        return functionDtos;
    }

}
