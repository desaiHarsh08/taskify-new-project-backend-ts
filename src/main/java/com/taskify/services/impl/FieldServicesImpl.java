package com.taskify.services.impl;

import com.taskify.constants.ActionType;
import com.taskify.constants.ResourceType;
import com.taskify.dtos.ColumnDto;
import com.taskify.dtos.FieldDto;
import com.taskify.dtos.FileDto;
import com.taskify.exceptions.ResourceNotFoundException;
import com.taskify.models.FieldModel;
import com.taskify.models.FunctionModel;
import com.taskify.models.TaskifyTimelineModel;
import com.taskify.models.UserModel;
import com.taskify.models.prototypes.FieldPrototypeModel;
import com.taskify.repositories.FieldRepository;
import com.taskify.repositories.FunctionRepository;
import com.taskify.repositories.TaskifyTimelineRepository;
import com.taskify.repositories.UserRepository;
import com.taskify.repositories.prototypes.FieldPrototypeRepository;
import com.taskify.services.ColumnServices;
import com.taskify.services.FieldServices;
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
public class FieldServicesImpl implements FieldServices {

    private static final int PAGE_SIZE = 25;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private FieldPrototypeRepository fieldPrototypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private ColumnServices columnServices;

    @Autowired
    private TaskifyTimelineRepository taskifyTimelineRepository;

    @Override
    public FieldDto createField(FieldDto field) {
        FieldModel fieldModel = this.modelMapper.map(field, FieldModel.class);
        // Check for field_prototype
        FieldPrototypeModel fieldPrototypeModel = this.fieldPrototypeRepository.findById(field.getFieldPrototypeId())
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "No field_prototype exist for id: " + field.getFieldPrototypeId()));

        // Check for function
        FunctionModel functionModel = this.functionRepository.findById(field.getFunctionId()).orElseThrow(
                () -> new IllegalArgumentException("No function exist for id: " + field.getFunctionId()));

        // Check for created_by_user
        UserModel createdByUser = this.userRepository.findById(field.getCreatedByUserId()).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + field.getCreatedByUserId()));

        fieldModel.setFieldPrototype(fieldPrototypeModel);
        fieldModel.setCreatedByUser(createdByUser);
        fieldModel.setFunction(functionModel);
        fieldModel.setCreatedDate(LocalDateTime.now());
        fieldModel.setLastEdited(LocalDateTime.now());
        fieldModel.setClosedByUser(null);
        fieldModel.setClosed(false);
        fieldModel.setClosedDate(null);

        // Create the field
        fieldModel = this.fieldRepository.save(fieldModel);
        System.out.println(fieldModel);

        // Create the column
        for (ColumnDto columnDto : field.getColumns()) {
            columnDto.setFieldId(fieldModel.getId());
            this.columnServices.createColumn(columnDto);
        }

        // Log the action
        TaskifyTimelineModel taskifyTimelineModel = new TaskifyTimelineModel();
        taskifyTimelineModel.setUser(createdByUser);
        taskifyTimelineModel.setResourceType(ResourceType.FIELD.name());
        taskifyTimelineModel.setActionType(ActionType.CREATE.name());
        taskifyTimelineModel.setAtDate(fieldModel.getCreatedDate());
        this.taskifyTimelineRepository.save(taskifyTimelineModel);

        return this.getFieldById(fieldModel.getId());
    }

    @Override
    public PageResponse<FieldDto> getAllFields(int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0");
        }

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<FieldModel> pageField = this.fieldRepository.findAll(pageable);

        List<FieldModel> fieldModels = pageField.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageField.getTotalPages(),
                pageField.getTotalElements(),
                this.fieldModelsToDtos(fieldModels));
    }

    @Override
    public FieldDto getFieldById(Long id) {
        FieldModel fieldModel = this.fieldRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No field exist for id: " + id));

        return this.fieldModelToDto(fieldModel);
    }

    @Override
    public PageResponse<FieldDto> getFieldsByFieldPrototypeId(int pageNumber, Long fieldPrototypeId) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should be always greater than 0");
        }

        FieldPrototypeModel foundFieldPrototypeModel = this.fieldPrototypeRepository.findById(fieldPrototypeId)
                .orElseThrow(
                        () -> new IllegalArgumentException("No field_prototype exist for id: " + fieldPrototypeId));

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<FieldModel> pageField = this.fieldRepository.findByFieldPrototype(pageable, foundFieldPrototypeModel);

        List<FieldModel> fieldModels = pageField.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageField.getTotalPages(),
                pageField.getTotalElements(),
                this.fieldModelsToDtos(fieldModels));
    }

    @Override
    public List<FieldDto> getFieldsByFunctionId(Long functionId) {
        FunctionModel foundFunctionModel = this.functionRepository.findById(functionId).orElseThrow(
                () -> new IllegalArgumentException("No function exist for id: " + functionId));

        List<FieldModel> fieldModels = this.fieldRepository.findByFunction(foundFunctionModel);

        return this.fieldModelsToDtos(fieldModels);
    }

    @Override
    public PageResponse<FieldDto> getFieldsByCreatedByUserId(int pageNumber, Long createdByUserId) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should be always greater than 0");
        }

        UserModel foundCreatedByUser = this.userRepository.findById(createdByUserId).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + createdByUserId));

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<FieldModel> pageField = this.fieldRepository.findByCreatedByUser(pageable, foundCreatedByUser);

        List<FieldModel> fieldModels = pageField.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageField.getTotalPages(),
                pageField.getTotalElements(),
                this.fieldModelsToDtos(fieldModels));
    }

    @Override
    public PageResponse<FieldDto> getFieldsByCreatedDate(int pageNumber, Date createdDate) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should be always greater than 0");
        }

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<FieldModel> pageField = this.fieldRepository.findByCreatedDate(pageable, createdDate);

        List<FieldModel> fieldModels = pageField.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageField.getTotalPages(),
                pageField.getTotalElements(),
                this.fieldModelsToDtos(fieldModels));
    }

    @Override
    public PageResponse<FieldDto> getFieldsByClosedByUserId(int pageNumber, Long closedByUserId) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should be always greater than 0");
        }

        UserModel foundClosedByUser = this.userRepository.findById(closedByUserId).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + closedByUserId));

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<FieldModel> pageField = this.fieldRepository.findByClosedByUser(pageable, foundClosedByUser);

        List<FieldModel> fieldModels = pageField.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageField.getTotalPages(),
                pageField.getTotalElements(),
                this.fieldModelsToDtos(fieldModels));
    }

    @Override
    public PageResponse<FieldDto> getFieldsByClosedDate(int pageNumber, Date closedDate) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should be always greater than 0");
        }

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<FieldModel> pageField = this.fieldRepository.findByClosedDate(pageable, closedDate);

        List<FieldModel> fieldModels = pageField.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageField.getTotalPages(),
                pageField.getTotalElements(),
                this.fieldModelsToDtos(fieldModels));
    }

    @Override
    public PageResponse<FieldDto> getFieldsByIsClosed(int pageNumber, boolean isClosed) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should be always greater than 0");
        }

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<FieldModel> pageField = this.fieldRepository.findByIsClosed(pageable, isClosed);

        List<FieldModel> fieldModels = pageField.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageField.getTotalPages(),
                pageField.getTotalElements(),
                this.fieldModelsToDtos(fieldModels));
    }

    @Override
    public FieldDto closeField(FieldDto givenField, Long userId) {
        FieldModel foundFieldModel = this.fieldRepository.findById(givenField.getId()).orElseThrow(
                () -> new ResourceNotFoundException("No field exist for id: " + givenField.getId()));

        foundFieldModel.setClosed(true);
        foundFieldModel.setClosedDate(LocalDateTime.now());

        UserModel foundClosedByUser = this.userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + givenField.getClosedByUserId()));

        foundFieldModel.setClosedByUser(foundClosedByUser);
        foundFieldModel.setLastEdited(foundFieldModel.getClosedDate());
        foundFieldModel = this.fieldRepository.save(foundFieldModel);
        System.out.println(foundFieldModel.isClosed());

        // Log the action
        TaskifyTimelineModel taskifyTimelineModel = new TaskifyTimelineModel();
        taskifyTimelineModel.setUser(foundClosedByUser);
        taskifyTimelineModel.setResourceType(ResourceType.FIELD.name());
        taskifyTimelineModel.setActionType(ActionType.CLOSED.name());
        taskifyTimelineModel.setAtDate(foundFieldModel.getClosedDate());
        this.taskifyTimelineRepository.save(taskifyTimelineModel);

        // TODO: Notify the user

        return this.fieldModelToDto(foundFieldModel);
    }

    @Override
    public boolean deleteField(Long id, Long userId) {
        FieldDto foundFieldDto = this.getFieldById(id);
        // Delete all the columns
        for (ColumnDto columnDto : foundFieldDto.getColumns()) {
            this.columnServices.deleteColumn(columnDto.getId());
        }

        UserModel foundUser = this.userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + userId));

        // Delete the field
        this.fieldRepository.deleteById(id);

        // Log the action
        TaskifyTimelineModel taskifyTimelineModel = new TaskifyTimelineModel();
        taskifyTimelineModel.setUser(foundUser);
        taskifyTimelineModel.setResourceType(ResourceType.FIELD.name());
        taskifyTimelineModel.setActionType(ActionType.DELETE.name());
        taskifyTimelineModel.setAtDate(LocalDateTime.now());
        this.taskifyTimelineRepository.save(taskifyTimelineModel);

        return true; // Success!
    }

    private FieldDto fieldModelToDto(FieldModel fieldModel) {
        if (fieldModel == null) {
            return null;
        }

        FieldDto fieldDto = this.modelMapper.map(fieldModel, FieldDto.class);
        fieldDto.setIsClosed(fieldModel.isClosed());
        fieldDto.setFieldPrototypeId(fieldModel.getFieldPrototype().getId());
        fieldDto.setFunctionId(fieldModel.getFunction().getId());
        fieldDto.setCreatedByUserId(fieldModel.getCreatedByUser().getId());
        if (fieldModel.getClosedByUser() != null) {
            fieldDto.setClosedByUserId(fieldModel.getClosedByUser().getId());
        }
        fieldDto.setColumns(this.columnServices.getColumnsByFieldId(fieldDto.getId()));

        return fieldDto;
    }

    private List<FieldDto> fieldModelsToDtos(List<FieldModel> fieldModels) {
        List<FieldDto> fieldDtos = new ArrayList<>();

        if (fieldModels != null) {
            for (FieldModel fieldModel : fieldModels) {
                fieldDtos.add(this.fieldModelToDto(fieldModel));
            }
        }

        return fieldDtos;
    }
}
