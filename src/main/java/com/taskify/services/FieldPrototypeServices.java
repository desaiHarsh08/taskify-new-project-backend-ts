package com.taskify.services;

import com.taskify.dtos.prototypes.FieldPrototypeDto;
import com.taskify.models.prototypes.FieldPrototypeModel;

public interface FieldPrototypeServices {

    FieldPrototypeDto createFieldPrototype(FieldPrototypeDto fieldPrototypeDto);

    FieldPrototypeDto getFieldPrototypeById(Long id);

    FieldPrototypeDto updateFieldPrototype(FieldPrototypeDto givenFieldPrototype);

    boolean deleteFieldPrototype(Long id);

    FieldPrototypeDto fieldPrototypeModelToDto(FieldPrototypeModel fieldPrototypeModel);

}
