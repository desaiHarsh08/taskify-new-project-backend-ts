package com.taskify.services;

import com.taskify.dtos.prototypes.FieldColumnPrototypeDto;

public interface FieldColumnPrototypeServices {

    FieldColumnPrototypeDto createFieldColumnPrototype(FieldColumnPrototypeDto fieldColumnPrototypeDto);

    boolean deleteFieldColumnPrototype(Long fieldColumnPrototypeId);

}
