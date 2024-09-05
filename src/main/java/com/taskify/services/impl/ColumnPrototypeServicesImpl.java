package com.taskify.services.impl;

import com.taskify.constants.ColumnType;
import com.taskify.dtos.prototypes.ColumnPrototypeDto;
import com.taskify.exceptions.ResourceNotFoundException;
import com.taskify.models.prototypes.ColumnPrototypeModel;
import com.taskify.repositories.prototypes.ColumnPrototypeRepository;
import com.taskify.services.ColumnPrototypeServices;

import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ColumnPrototypeServicesImpl implements ColumnPrototypeServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ColumnPrototypeRepository columnPrototypeRepository;

    @Transactional
    @Override
    public ColumnPrototypeDto createColumnPrototype(ColumnPrototypeDto columnPrototypeDto) {
        // Check for column already exist
        ColumnPrototypeModel foundColumnPrototypeModel = this.columnPrototypeRepository.findByName(columnPrototypeDto.getName()).orElse(null);
        System.out.println("Found column:" + foundColumnPrototypeModel);
        if (foundColumnPrototypeModel != null) {
            return this.columnPrototypeModelToDto(foundColumnPrototypeModel);
        }

        // Check if the column_type is valid
        if (
                !columnPrototypeDto.getColumnType().equalsIgnoreCase(ColumnType.STRING.name()) &&
                !columnPrototypeDto.getColumnType().equalsIgnoreCase(ColumnType.NUMBER.name()) &&
                !columnPrototypeDto.getColumnType().equalsIgnoreCase(ColumnType.BOOLEAN.name()) &&
                !columnPrototypeDto.getColumnType().equalsIgnoreCase(ColumnType.FILE.name()) &&
                !columnPrototypeDto.getColumnType().equalsIgnoreCase(ColumnType.DATE.name())
        ) {
            throw new IllegalArgumentException("Invalid column_type provided...");
        }

        // Create the column_prototype
        ColumnPrototypeModel savedColumnPrototype = this.columnPrototypeRepository.save(this.modelMapper.map(columnPrototypeDto, ColumnPrototypeModel.class));

        columnPrototypeDto.setId(savedColumnPrototype.getId());

        System.out.println("col-prototype saved!");

        return columnPrototypeDto;
    }

    @Override
    public ColumnPrototypeDto getColumnPrototypeById(Long id) {
        ColumnPrototypeModel foundColumnPrototypeModel = this.columnPrototypeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No column_prototype exist for id: " + id)
        );

        return this.columnPrototypeModelToDto(foundColumnPrototypeModel);
    }

    @Override
    public ColumnPrototypeDto updateColumnPrototype(ColumnPrototypeDto givenColumnPrototype) {
        ColumnPrototypeModel foundColumnPrototypeModel = this.columnPrototypeRepository.findById(givenColumnPrototype.getId()).orElseThrow(
                () -> new ResourceNotFoundException("No column_prototype exist for id: " + givenColumnPrototype.getId())
        );

        // Check if the column_type is valid
        if (
                !givenColumnPrototype.getColumnType().equalsIgnoreCase(ColumnType.STRING.name()) &&
                        !givenColumnPrototype.getColumnType().equalsIgnoreCase(ColumnType.NUMBER.name()) &&
                        !givenColumnPrototype.getColumnType().equalsIgnoreCase(ColumnType.BOOLEAN.name()) &&
                        !givenColumnPrototype.getColumnType().equalsIgnoreCase(ColumnType.FILE.name())
        ) {
            throw new IllegalArgumentException("Invalid column_type provided...");
        }

        // Update the data
        foundColumnPrototypeModel.setName(givenColumnPrototype.getName());
        foundColumnPrototypeModel.setColumnType(givenColumnPrototype.getColumnType());
        foundColumnPrototypeModel.setLargeText(givenColumnPrototype.isLargeText());
        foundColumnPrototypeModel.setMultipleFiles(givenColumnPrototype.isMultipleFiles());

        // Save the data
        this.columnPrototypeRepository.save(foundColumnPrototypeModel);

        return this.getColumnPrototypeById(givenColumnPrototype.getId());
    }

    @Override
    public boolean deleteColumnPrototype(Long id) {
        // Check for the column_prototype
        this.getColumnPrototypeById(id);

        // Delete the column_prototype
        this.columnPrototypeRepository.deleteById(id);

        return false; // Success!
    }

    private ColumnPrototypeDto columnPrototypeModelToDto(ColumnPrototypeModel columnPrototypeModel) {
        if (columnPrototypeModel == null) {
            return null;
        }
        ColumnPrototypeDto columnPrototypeDto = this.modelMapper.map(columnPrototypeModel, ColumnPrototypeDto.class);

        columnPrototypeDto.setId(columnPrototypeModel.getId());

        return columnPrototypeDto;
    }

    @SuppressWarnings("unused")
    private List<ColumnPrototypeDto> columnPrototypeModelsToDtos(List<ColumnPrototypeModel> columnPrototypeModels) {
        if (columnPrototypeModels == null || columnPrototypeModels.isEmpty()) {
            return new ArrayList<>();
        }

        List<ColumnPrototypeDto> columnPrototypeDtos = new ArrayList<>();

        for (ColumnPrototypeModel columnPrototypeModel: columnPrototypeModels) {
            columnPrototypeDtos.add(this.columnPrototypeModelToDto(columnPrototypeModel));
        }

        return columnPrototypeDtos;
    }
}
