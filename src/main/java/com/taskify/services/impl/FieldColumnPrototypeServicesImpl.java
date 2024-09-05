package com.taskify.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskify.dtos.prototypes.FieldColumnPrototypeDto;
// import com.taskify.models.prototypes.ColumnPrototypeModel;
import com.taskify.models.prototypes.FieldPrototypeModel;
// import com.taskify.repositories.prototypes.ColumnPrototypeRepository;
import com.taskify.repositories.prototypes.FieldPrototypeRepository;
import com.taskify.services.FieldColumnPrototypeServices;

@Service
public class FieldColumnPrototypeServicesImpl implements FieldColumnPrototypeServices {

    @Autowired
    private FieldPrototypeRepository fieldPrototypeRepository;

    // @Autowired
    // private ColumnPrototypeRepository columnPrototypeRepository;

    @Override
    public FieldColumnPrototypeDto createFieldColumnPrototype(FieldColumnPrototypeDto fieldColumnPrototypeDto) {
        // Check for column_prototype
        // ColumnPrototypeModel foundColumnPrototype = this.columnPrototypeRepository.findById(fieldColumnPrototypeDto.getColumnPrototypeId()).orElse(null);
        // Check for field_prototype
        FieldPrototypeModel foundFieldPrototype = this.fieldPrototypeRepository.findById(fieldColumnPrototypeDto.getFieldPrototypeId()).orElse(null);
        // TODO: Create field_prototype
        if (foundFieldPrototype == null) {

        }
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createFieldColumnPrototype'");
    }

    @Override
    public boolean deleteFieldColumnPrototype(Long fieldColumnPrototypeId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteFieldColumnPrototype'");
    }

}
