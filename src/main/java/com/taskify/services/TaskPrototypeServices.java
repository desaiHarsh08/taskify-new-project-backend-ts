package com.taskify.services;

import com.taskify.dtos.prototypes.FunctionPrototypeDto;
import com.taskify.dtos.prototypes.TaskPrototypeDto;
import com.taskify.utils.PageResponse;

public interface TaskPrototypeServices {

    TaskPrototypeDto createTaskPrototype(TaskPrototypeDto taskPrototypeDto);

    public TaskPrototypeDto addFunctionPrototypeToTaskPrototype(Long taskPrototypeId, FunctionPrototypeDto newFunctionPrototypeDto);

    PageResponse<TaskPrototypeDto> getAllTaskPrototypes(int pageNumber);

    TaskPrototypeDto getTaskPrototypeById(Long id);

    TaskPrototypeDto updateTaskPrototype(TaskPrototypeDto givenTaskPrototype, Long userId);

    boolean deleteTaskPrototype(Long id, Long userId);

}
