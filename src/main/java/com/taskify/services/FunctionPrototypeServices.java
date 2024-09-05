package com.taskify.services;

import com.taskify.dtos.prototypes.FunctionPrototypeDto;
import com.taskify.models.prototypes.FunctionPrototypeModel;
import com.taskify.utils.PageResponse;

import java.util.List;
import java.util.Set;

public interface FunctionPrototypeServices {

    FunctionPrototypeDto createFunctionPrototype(FunctionPrototypeDto functionPrototypeDto);

    PageResponse<FunctionPrototypeDto> getAllFunctionPrototypes(int pageNumber);

    List<FunctionPrototypeDto> getFunctionPrototypesByDepartment(String department);

    FunctionPrototypeDto getFunctionPrototypeById(Long id);

    FunctionPrototypeDto getFunctionPrototypeByTitle(String title);

    FunctionPrototypeDto updateFunctionPrototype(FunctionPrototypeDto givenFunctionPrototypeDto);

    boolean deleteFunctionPrototype(Long id);

    FunctionPrototypeDto functionPrototypeModelToDto(FunctionPrototypeModel functionPrototypeModel);

    Set<FunctionPrototypeDto> functionPrototypeModelsToDtos(Set<FunctionPrototypeModel> functionPrototypeModels);

}
