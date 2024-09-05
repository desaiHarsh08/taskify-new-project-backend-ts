package com.taskify.services;

import com.taskify.dtos.FileDto;
import com.taskify.dtos.FunctionDto;
import com.taskify.utils.PageResponse;

import java.util.Date;
import java.util.List;

public interface FunctionServices {

    FunctionDto createFunction(FunctionDto functionDto);

    PageResponse<FunctionDto> getAllFunctions(int pageNumber);

    FunctionDto getFunctionById(Long id);

    List<FunctionDto> getFunctionsByTaskId(Long taskId);

    PageResponse<FunctionDto> getFunctionsByDepartment(int pageNumber, String department);

    PageResponse<FunctionDto> getFunctionsByCreatedByUser(int pageNumber, Long createdByUserId);

    PageResponse<FunctionDto> getFunctionsByCreatedDate(int pageNumber, Date createdDate);

    PageResponse<FunctionDto> getFunctionsByDueDate(int pageNumber, Date dueDate);

    PageResponse<FunctionDto> getFunctionsByClosedByUserId(int pageNumber, Long closedByUserId);

    PageResponse<FunctionDto> getFunctionsByClosedDate(int pageNumber, Date closedDate);

    PageResponse<FunctionDto> getFunctionsByIsClosed(int pageNumber, boolean isClosed);

    FunctionDto updateFunction(FunctionDto givenFunctionDto, Long userId);

    FunctionDto closeFunction(FunctionDto givenFunctionDto, Long userId);

    boolean deleteFunction(Long id, Long userId);


}
