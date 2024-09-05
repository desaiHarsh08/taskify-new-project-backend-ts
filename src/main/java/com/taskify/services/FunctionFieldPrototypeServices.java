package com.taskify.services;

import com.taskify.dtos.prototypes.FunctionFieldPrototypeDto;

public interface FunctionFieldPrototypeServices {

    FunctionFieldPrototypeDto createFunctionField(FunctionFieldPrototypeDto functionFieldPrototypeDto);

    boolean deleteFunctionField(Long id);

}
