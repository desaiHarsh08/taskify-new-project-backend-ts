package com.taskify.services;

import com.taskify.dtos.TaskDto;
import com.taskify.utils.MonthlyStats;
import com.taskify.utils.PageResponse;
import com.taskify.utils.TaskStats;

import java.util.Date;
import java.util.List;

public interface TaskServices {

    TaskDto createTask(TaskDto taskDto);

    PageResponse<TaskDto> getAllTasks(int pageNumber);

    PageResponse<TaskDto> getTasksByPrototypeId(int pageNumber, Long taskPrototypeId);

    TaskDto getTasksById(Long id);

    PageResponse<TaskDto> getTasksByPriority(int pageNumber, String priorityType);

    PageResponse<TaskDto> getTasksByCreatedUser(int pageNumber, Long createdByUserId);

    PageResponse<TaskDto> getTasksByAssignedUser(int pageNumber, Long assignedByUserId);

    PageResponse<TaskDto> getTasksByCreatedDate(int pageNumber, Date createdDate);

    PageResponse<TaskDto> getTasksByClosedDate(int pageNumber, Date closedDate);

    PageResponse<TaskDto> getTasksByIsClosed(int pageNumber, boolean isClosed);

    PageResponse<TaskDto> getTasksByClosedByUserId(int pageNumber, Long closedByUserId);

    List<TaskDto> getTasksByCustomer(Long customerId);

    TaskDto updateTask(TaskDto givenTaskDto, Long userId);

    TaskDto closeTask(TaskDto givenTaskDto);

    TaskDto getTaskByAbbreviation(String taskAbbreviation);

    boolean deleteTask(Long id, Long userId);

    Long getTotalTasksByYearAndMonth(int year, int month);

    TaskStats totalStats();

    MonthlyStats monthlyStats();

}
