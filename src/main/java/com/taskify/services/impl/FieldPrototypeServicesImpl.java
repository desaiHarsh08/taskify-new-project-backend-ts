package com.taskify.services.impl;

import com.taskify.dtos.prototypes.ColumnPrototypeDto;
import com.taskify.dtos.prototypes.FieldPrototypeDto;
import com.taskify.exceptions.ResourceNotFoundException;
import com.taskify.models.prototypes.ColumnPrototypeModel;
import com.taskify.models.prototypes.FieldColumnPrototypeModel;
import com.taskify.models.prototypes.FieldPrototypeModel;
import com.taskify.repositories.prototypes.FieldColumnProtototypeRepository;
import com.taskify.repositories.prototypes.FieldPrototypeRepository;
import com.taskify.services.ColumnPrototypeServices;
import com.taskify.services.FieldPrototypeServices;

import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FieldPrototypeServicesImpl implements FieldPrototypeServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FieldPrototypeRepository fieldPrototypeRepository;

    @Autowired
    private ColumnPrototypeServices columnPrototypeServices;

    @Autowired
    private FieldColumnProtototypeRepository fieldColumnProtototypeRepository;

    @Transactional
    @Override
    public FieldPrototypeDto createFieldPrototype(FieldPrototypeDto fieldPrototypeDto) {
        // Check if the field already exist
        FieldPrototypeModel foundFieldPrototypeModel = this.fieldPrototypeRepository.findByTitle(fieldPrototypeDto.getTitle()).orElse(null);
        if (foundFieldPrototypeModel != null) {
            return this.fieldPrototypeModelToDto(foundFieldPrototypeModel);
        }

        // Create the field_prototype
        FieldPrototypeModel fieldPrototypeModel = this.modelMapper.map(fieldPrototypeDto, FieldPrototypeModel.class);
        fieldPrototypeModel = this.fieldPrototypeRepository.save(fieldPrototypeModel);
        System.out.println("field-prototype saved!");

        System.out.println("columns given: " + fieldPrototypeDto.getColumnPrototypes());
        // Add the columns
        for (ColumnPrototypeDto columnPrototypeDto: fieldPrototypeDto.getColumnPrototypes()) {
            System.out.println("Saving column...");
            // Create column
            ColumnPrototypeDto savedColumnPrototypeDto = this.columnPrototypeServices.createColumnPrototype(columnPrototypeDto);
            System.out.println("Column got in field_prototype: " + savedColumnPrototypeDto);
            // Convert to columnModel
            ColumnPrototypeModel columnPrototypeModel = this.modelMapper.map(savedColumnPrototypeDto, ColumnPrototypeModel.class);

            // Create the field columns
            FieldColumnPrototypeModel foundFieldColumnPrototype = this.fieldColumnProtototypeRepository.findByFieldPrototypeAndColumnPrototype(fieldPrototypeModel, columnPrototypeModel).orElse(null);
            if (foundFieldColumnPrototype != null) { // Already exist
                continue;
            }
            FieldColumnPrototypeModel fieldColumnPrototypeModel = new FieldColumnPrototypeModel(null, fieldPrototypeModel, columnPrototypeModel);
            this.fieldColumnProtototypeRepository.save(fieldColumnPrototypeModel);
        }

        return this.fieldPrototypeModelToDto(fieldPrototypeModel);
    }


    @Override
    public FieldPrototypeDto getFieldPrototypeById(Long id) {
        FieldPrototypeModel foundFieldPrototype = this.fieldPrototypeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No field_prototype exist for id: " + id)
        );

        return this.fieldPrototypeModelToDto(foundFieldPrototype);
    }

    @Override
    public FieldPrototypeDto updateFieldPrototype(FieldPrototypeDto givenFieldPrototype) {
        // Check for field_prototype
        FieldPrototypeModel foundFieldPrototype = this.fieldPrototypeRepository.findById(givenFieldPrototype.getId()).orElseThrow(
                () -> new ResourceNotFoundException("No field_prototype exist for id: " + givenFieldPrototype.getId())
        );

        // Update the data for field_prototype
        foundFieldPrototype.setTitle(givenFieldPrototype.getTitle());
        foundFieldPrototype.setDescription(givenFieldPrototype.getDescription());

        // Save the changes
        this.fieldPrototypeRepository.save(foundFieldPrototype);

        return this.getFieldPrototypeById(givenFieldPrototype.getId());
    }

    @Override
    public boolean deleteFieldPrototype(Long id) {
        FieldPrototypeDto foundFieldPrototype = this.getFieldPrototypeById(id);
        
        FieldPrototypeModel fieldPrototypeModel = this.modelMapper.map(foundFieldPrototype, FieldPrototypeModel.class);

        // Delete all the field_column_prototypes
        List<FieldColumnPrototypeModel> fieldColumnPrototypeModels = this.fieldColumnProtototypeRepository.findByFieldPrototype(fieldPrototypeModel);
        for (FieldColumnPrototypeModel fieldColumnPrototypeModel: fieldColumnPrototypeModels) {
            this.fieldColumnProtototypeRepository.deleteById(fieldColumnPrototypeModel.getId());
        }

        // Delete the field_prototype
        this.fieldPrototypeRepository.deleteById(id);

        return true; // Success!
    }

    @Override
    public FieldPrototypeDto fieldPrototypeModelToDto(FieldPrototypeModel fieldPrototypeModel) {
        if (fieldPrototypeModel == null) {
            return null;
        }
        FieldPrototypeDto fieldPrototypeDto = this.modelMapper.map(fieldPrototypeModel, FieldPrototypeDto.class);
        fieldPrototypeDto.setId(fieldPrototypeModel.getId());

        // Fetch all the column_prototypes
        List<ColumnPrototypeDto> columnPrototypeDtos = new ArrayList<>();
        List<FieldColumnPrototypeModel> fieldColumnPrototypeModels = this.fieldColumnProtototypeRepository.findByFieldPrototype(fieldPrototypeModel);
        for (FieldColumnPrototypeModel fieldColumnPrototypeModel: fieldColumnPrototypeModels) {
            ColumnPrototypeDto columnPrototypeDto = this.columnPrototypeServices.getColumnPrototypeById(fieldColumnPrototypeModel.getColumnPrototype().getId());
            if (columnPrototypeDto != null) {
                columnPrototypeDtos.add(columnPrototypeDto);
            }
        } 

        fieldPrototypeDto.setColumnPrototypes(columnPrototypeDtos);

        return fieldPrototypeDto;
    }

    @SuppressWarnings("unused")
    private List<FieldPrototypeDto> fieldPrototypeModelsToDtos(List<FieldPrototypeModel> fieldPrototypeModels) {
        if (fieldPrototypeModels == null || fieldPrototypeModels.isEmpty()) {
            return new ArrayList<>();
        }

        List<FieldPrototypeDto> fieldPrototypeDtos = new ArrayList<>();

        for (FieldPrototypeModel fieldPrototypeModel: fieldPrototypeModels) {
            fieldPrototypeDtos.add(this.fieldPrototypeModelToDto(fieldPrototypeModel));
        }

        return fieldPrototypeDtos;
    }
}
