package com.taskify.services;

import com.taskify.dtos.prototypes.ColumnPrototypeDto;

public interface ColumnPrototypeServices {

    ColumnPrototypeDto createColumnPrototype(ColumnPrototypeDto columnPrototypeDto);

    ColumnPrototypeDto getColumnPrototypeById(Long id);

    ColumnPrototypeDto updateColumnPrototype(ColumnPrototypeDto givenColumnPrototype);

    boolean deleteColumnPrototype(Long id);
    
}
