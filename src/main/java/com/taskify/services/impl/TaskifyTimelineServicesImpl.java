package com.taskify.services.impl;

import com.taskify.dtos.TaskifyTimelineDto;
import com.taskify.exceptions.ResourceNotFoundException;
import com.taskify.models.*;
import com.taskify.repositories.*;
import com.taskify.services.TaskifyTimelineServices;
import com.taskify.utils.PageResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TaskifyTimelineServicesImpl implements TaskifyTimelineServices {

    private static final int PAGE_SIZE = 500;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskifyTimelineRepository taskifyTimelineRepository;

    // @Autowired
    // private TaskRepository taskRepository;

    // @Autowired
    // private FunctionRepository functionRepository;

    // @Autowired
    // private FieldRepository fieldRepository;

    @Override
    public TaskifyTimelineDto createTaskifyTimeline(TaskifyTimelineDto taskifyTimelineDto) {
        // TaskifyTimelineModel taskifyTimelineModel =
        // this.modelMapper.map(taskifyTimelineDto, TaskifyTimelineModel.class);
        // if (taskifyTimelineDto.getTaskId() != null) {
        // TaskModel taskModel = new TaskModel();
        // taskModel.setId(taskifyTimelineDto.getTaskId());
        // taskifyTimelineModel.setTask(taskModel);
        // }
        // if (taskifyTimelineDto.getFunctionId() != null) {
        // FunctionModel functionModel = new FunctionModel();
        // functionModel.setId(taskifyTimelineDto.getFunctionId());
        // taskifyTimelineModel.setFunction(functionModel);
        // }
        // if (taskifyTimelineDto.getFieldId() != null) {
        // FieldModel fieldModel = new FieldModel();
        // fieldModel.setId(taskifyTimelineDto.getFieldId());
        // taskifyTimelineModel.setField(fieldModel);
        // }

        // taskifyTimelineModel.setAtDate(new Date());
        // UserModel userModel = new UserModel();
        // userModel.setId(taskifyTimelineDto.getUserId());
        // taskifyTimelineModel.setUser(userModel);

        // taskifyTimelineModel =
        // this.taskifyTimelineRepository.save(taskifyTimelineModel);

        // return this.taskifyTimelineModelToDto(taskifyTimelineModel);
        return null;
    }

    @Override
    public PageResponse<TaskifyTimelineDto> getAllTaskifyTimelines(int pageNumber) {
        if (pageNumber < 0) {
            throw new ResourceNotFoundException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<TaskifyTimelineModel> pageTaskifyTimeline = this.taskifyTimelineRepository.findAll(pageable);

        List<TaskifyTimelineModel> taskifyTimelineModels = pageTaskifyTimeline.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTaskifyTimeline.getTotalPages(),
                pageTaskifyTimeline.getTotalElements(),
                this.taskifyTimelineModelsToDtos(taskifyTimelineModels));
    }

    @Override
    public PageResponse<TaskifyTimelineDto> getUserHistory(int pageNumber, Long userId) {
        if (pageNumber < 0) {
            throw new ResourceNotFoundException("Page should always be greater than 0.");
        }

        UserModel foundUser = this.userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("No user exist for id: " + userId));

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<TaskifyTimelineModel> pageTaskifyTimeline = this.taskifyTimelineRepository.findByUser(pageable, foundUser);

        List<TaskifyTimelineModel> taskifyTimelineModels = pageTaskifyTimeline.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTaskifyTimeline.getTotalPages(),
                pageTaskifyTimeline.getTotalElements(),
                this.taskifyTimelineModelsToDtos(taskifyTimelineModels));
    }

    @Override
    public PageResponse<TaskifyTimelineDto> getTaskifyTimelinesByResourceType(int pageNumber, String resourceType) {
        if (pageNumber < 0) {
            throw new ResourceNotFoundException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<TaskifyTimelineModel> pageTaskifyTimeline = this.taskifyTimelineRepository.findByResourceType(pageable,
                resourceType);

        List<TaskifyTimelineModel> taskifyTimelineModels = pageTaskifyTimeline.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTaskifyTimeline.getTotalPages(),
                pageTaskifyTimeline.getTotalElements(),
                this.taskifyTimelineModelsToDtos(taskifyTimelineModels));
    }

    @Override
    public PageResponse<TaskifyTimelineDto> getTaskifyTimelinesByActionType(int pageNumber, String actionType) {
        if (pageNumber < 0) {
            throw new ResourceNotFoundException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<TaskifyTimelineModel> pageTaskifyTimeline = this.taskifyTimelineRepository.findByActionType(pageable,
                actionType);

        List<TaskifyTimelineModel> taskifyTimelineModels = pageTaskifyTimeline.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTaskifyTimeline.getTotalPages(),
                pageTaskifyTimeline.getTotalElements(),
                this.taskifyTimelineModelsToDtos(taskifyTimelineModels));
    }

    @Override
    public PageResponse<TaskifyTimelineDto> getTaskifyTimelinesByMonthAndYear(int pageNumber, int month, int year) {
        if (pageNumber < 0) {
            throw new ResourceNotFoundException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));
        
        Page<TaskifyTimelineModel> pageTaskifyTimeline = this.taskifyTimelineRepository.findByMonthAndYear(pageable, month, year);

        List<TaskifyTimelineModel> taskifyTimelineModels = pageTaskifyTimeline.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTaskifyTimeline.getTotalPages(),
                pageTaskifyTimeline.getTotalElements(),
                this.taskifyTimelineModelsToDtos(taskifyTimelineModels));
    }

    @Override
    public PageResponse<TaskifyTimelineDto> getTaskifyTimelinesByAtDate(int pageNumber, LocalDate date) {
        if (pageNumber < 0) {
            throw new ResourceNotFoundException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));
        
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        Page<TaskifyTimelineModel> pageTaskifyTimeline = this.taskifyTimelineRepository.findByAtDateBetween(pageable, startOfDay, endOfDay);

        List<TaskifyTimelineModel> taskifyTimelineModels = pageTaskifyTimeline.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTaskifyTimeline.getTotalPages(),
                pageTaskifyTimeline.getTotalElements(),
                this.taskifyTimelineModelsToDtos(taskifyTimelineModels));
    }

    @Override
    public PageResponse<TaskifyTimelineDto> getTaskifyTimelinesByTaskAbbreviation(int pageNumber,
            String taskAbbreviation) {
        if (pageNumber < 0) {
            throw new ResourceNotFoundException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<TaskifyTimelineModel> pageTaskifyTimeline = this.taskifyTimelineRepository.findByTaskAbbreviation(pageable,
                taskAbbreviation);

        List<TaskifyTimelineModel> taskifyTimelineModels = pageTaskifyTimeline.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTaskifyTimeline.getTotalPages(),
                pageTaskifyTimeline.getTotalElements(),
                this.taskifyTimelineModelsToDtos(taskifyTimelineModels));
    }

    @Override
    public PageResponse<TaskifyTimelineDto> getTaskifyTimelinesByFunctionName(int pageNumber, String functionName) {
        if (pageNumber < 0) {
            throw new ResourceNotFoundException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<TaskifyTimelineModel> pageTaskifyTimeline = this.taskifyTimelineRepository.findByFunctionName(pageable,
                functionName);

        List<TaskifyTimelineModel> taskifyTimelineModels = pageTaskifyTimeline.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTaskifyTimeline.getTotalPages(),
                pageTaskifyTimeline.getTotalElements(),
                this.taskifyTimelineModelsToDtos(taskifyTimelineModels));
    }

    private TaskifyTimelineDto taskifyTimelineModelToDto(TaskifyTimelineModel taskifyTimelineModel) {
        TaskifyTimelineDto taskifyTimelineDto = this.modelMapper.map(taskifyTimelineModel, TaskifyTimelineDto.class);
        taskifyTimelineDto.setUserId(taskifyTimelineModel.getUser().getId());

        return taskifyTimelineDto;
    }

    private List<TaskifyTimelineDto> taskifyTimelineModelsToDtos(List<TaskifyTimelineModel> taskifyTimelineModels) {
        List<TaskifyTimelineDto> taskifyTimelineDtos = new ArrayList<>();

        if (taskifyTimelineModels != null) {
            for (TaskifyTimelineModel taskifyTimelineModel : taskifyTimelineModels) {
                taskifyTimelineDtos.add(this.taskifyTimelineModelToDto(taskifyTimelineModel));
            }
        }

        return taskifyTimelineDtos;
    }
}
