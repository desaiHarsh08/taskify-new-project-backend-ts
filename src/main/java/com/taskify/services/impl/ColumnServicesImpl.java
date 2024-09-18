package com.taskify.services.impl;

import com.taskify.constants.ColumnType;
import com.taskify.dtos.ColumnDto;
import com.taskify.dtos.FileDto;
import com.taskify.exceptions.ResourceNotFoundException;
import com.taskify.models.*;
import com.taskify.models.prototypes.ColumnPrototypeModel;
import com.taskify.models.prototypes.TaskPrototypeModel;
import com.taskify.repositories.*;
import com.taskify.repositories.prototypes.ColumnPrototypeRepository;
import com.taskify.repositories.prototypes.TaskPrototypeRepository;
import com.taskify.services.ColumnServices;
import com.taskify.utils.PageResponse;

import org.springframework.web.multipart.MultipartFile;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ColumnServicesImpl implements ColumnServices {

    private static int PAGE_SIZE = 25;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ColumnRepository columnRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private ColumnPrototypeRepository columnPrototypeRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskPrototypeRepository taskPrototypeRepository;

    @Override
    public ColumnDto createColumn(ColumnDto column) {
        ColumnPrototypeModel foundColumnPrototypeModel = this.columnPrototypeRepository
                .findById(column.getColumnPrototypeId()).orElseThrow(
                        () -> new IllegalArgumentException(
                                "No column_prototype exist for id: " + column.getColumnPrototypeId()));

        FieldModel foundFieldModel = this.fieldRepository.findById(column.getFieldId()).orElseThrow(
                () -> new ResourceNotFoundException("No field exist for id: " + column.getFieldId()));

        ColumnModel columnModel = new ColumnModel();
        columnModel.setColumnPrototype(foundColumnPrototypeModel);
        columnModel.setField(foundFieldModel);

        // Create the column
        columnModel = this.columnRepository.save(columnModel);
        column.setId(columnModel.getId());

        if (foundColumnPrototypeModel.getColumnType().equals(ColumnType.NUMBER.name())) {
            columnModel.setNumberValue(column.getNumberValue());
        } 
        else if (foundColumnPrototypeModel.getColumnType().equals(ColumnType.STRING.name())) {
            columnModel.setTextValue(column.getTextValue());
        } 
        else if (foundColumnPrototypeModel.getColumnType().equals(ColumnType.BOOLEAN.name())) {
            columnModel.setBooleanValue(column.getBooleanValue());
        } 
        else if (foundColumnPrototypeModel.getColumnType().equals(ColumnType.DATE.name())) {
            columnModel.setDateValue(column.getDateValue());
        }

        // Save the column
        columnModel = this.columnRepository.save(columnModel);

        // TODO: Notify customer
        if (foundColumnPrototypeModel.isNotifyCustomer()) {

        }

        return this.columnModelToDto(columnModel);
    }

    @Override
    public boolean uploadFiles(ColumnDto columnDto, MultipartFile[] files) {
        ColumnModel columnModel = this.columnRepository.findById(columnDto.getId()).orElseThrow(
                () -> new IllegalArgumentException("No column exist for id: " + columnDto.getId()));

        // Create the directory path
        String directoryPath = this.getFilePath(columnModel);

        // Create the directory if it does not exist
        File directory = new File(directoryPath);
        System.out.println("Attempting for Creating directory: -");
        if (!directory.exists()) {

            if (!directory.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + directoryPath);
            }
        }

        System.out.println("created directory");
        for (MultipartFile file : files) {
            System.out.println("saving...");
            this.saveFile(file, directoryPath);
        }
        return true;
    }

    private void saveFile(MultipartFile file, String fileDirectory) {
        System.out.println(fileDirectory);
        // Define the directory path adjacent to the root directory
        try {
            Path directoryPath = Paths.get(fileDirectory);

            // Ensure the directory exists
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            System.out.println("directory exist, and now storing...");

            // Create the path for the file to be stored, using the provided fileName
            Path filePath = directoryPath.resolve(file.getOriginalFilename());

            System.out.println("Full file path: " + filePath.toAbsolutePath());

            // Save the file to the defined directory
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            e.printStackTrace();
            // throw new Exception("file not uploaded!");
        }
    }

    @Override
    public byte[] readFileAsBytes(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("File not found or is not a valid file: " + filePath);
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace(); // Consider logging the error
            return null;
        }
    }

    @Override
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        } else {
            throw new ResourceNotFoundException("File not found: " + filePath);
        }
    }

    @Override
    public PageResponse<ColumnDto> getAllColumns(int pageNumber) {
        return null;
    }

    @Override
    public ColumnDto getColumnById(Long id) {
        ColumnModel foundColumn = this.columnRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No column exist for id: " + id));

        return this.columnModelToDto(foundColumn);
    }

    @Override
    public PageResponse<ColumnDto> getColumnsByColumnPrototypeId(int pageNumber, Long columnPrototypeId) {
        ColumnPrototypeModel foundColumnPrototypeModel = this.columnPrototypeRepository.findById(columnPrototypeId)
                .orElseThrow(
                        () -> new IllegalArgumentException("No column_prototype exist for id: " + columnPrototypeId));

        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0");
        }

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by(Sort.Direction.DESC));

        Page<ColumnModel> pageColumn = this.columnRepository.findByColumnPrototype(pageable, foundColumnPrototypeModel);

        List<ColumnModel> columnModels = pageColumn.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageColumn.getTotalPages(),
                pageColumn.getTotalElements(),
                this.columnModelsToDtos(columnModels));
    }

    @Override
    public List<ColumnDto> getColumnsByFieldId(Long fieldId) {
        FieldModel foundFieldModel = this.fieldRepository.findById(fieldId).orElseThrow(
                () -> new IllegalArgumentException("No field exist for id: " + fieldId));

        List<ColumnModel> columnModels = this.columnRepository.findByField(foundFieldModel);

        return this.columnModelsToDtos(columnModels);
    }

    @Override
    public ColumnDto updateColumn(ColumnDto givenColumn) {
        System.out.println("column: " + givenColumn);
        ColumnModel foundColumn = this.columnRepository.findById(givenColumn.getId()).orElseThrow(
                () -> new ResourceNotFoundException("No column exist for id: " + givenColumn.getId()));

        ColumnPrototypeModel foundColumnPrototypeModel = this.columnPrototypeRepository
                .findById(foundColumn.getColumnPrototype().getId()).orElseThrow(
                        () -> new IllegalArgumentException(
                                "No column_prototype exist for id: " + foundColumn.getColumnPrototype().getId()));

        FieldModel foundField = this.fieldRepository.findById(givenColumn.getFieldId()).orElseThrow(
                () -> new IllegalArgumentException("No field exist for id: " + givenColumn.getFieldId()));

        // Update the changes
        // if
        // (foundColumnPrototypeModel.getColumnType().equals(ColumnType.NUMBER.name()))
        // {
        foundColumn.setNumberValue(givenColumn.getNumberValue());
        // } else if
        // (foundColumnPrototypeModel.getColumnType().equals(ColumnType.STRING.name()))
        // {
        foundColumn.setTextValue(givenColumn.getTextValue());
        foundColumn.setDateValue(givenColumn.getDateValue());
        // } else if
        // (foundColumnPrototypeModel.getColumnType().equals(ColumnType.BOOLEAN.name()))
        // {
        foundColumn.setBooleanValue(givenColumn.getBooleanValue());
        // }

        foundField.setLastEdited(LocalDateTime.now());

        // Save the changes
        this.columnRepository.save(foundColumn);
        this.fieldRepository.save(foundField);

        return this.columnModelToDto(foundColumn);
    }

    @Override
    public boolean deleteColumn(Long id) {
        // Retrieve the column model
        ColumnModel foundColumn = this.columnRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No column exist for id: " + id));

        // If the column's prototype type is FILE, handle file deletion
        if (foundColumn.getColumnPrototype().getColumnType().equals(ColumnType.FILE.name())) {
            String directoryPath = this.getFilePath(foundColumn);

            // Delete the files
            File directory = new File(directoryPath);
            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (!file.delete()) {
                            // Log an error if a file could not be deleted
                            System.err.println("Failed to delete file: " + file.getAbsolutePath());
                        }
                    }
                }
                // Optionally, delete the directory itself if it's empty
                if (directory.listFiles() == null || directory.listFiles().length == 0) {
                    if (!directory.delete()) {
                        // Log an error if the directory could not be deleted
                        System.err.println("Failed to delete directory: " + directory.getAbsolutePath());
                    }
                }
            }
        }

        // Delete the column from the database
        this.columnRepository.deleteById(id);

        return true; // Success!
    }

    private String getFilePath(ColumnModel columnModel) {
        FieldModel foundFieldModel = this.fieldRepository.findById(columnModel.getField().getId()).orElseThrow(
                () -> new IllegalArgumentException("No field exist for id: " + columnModel.getField().getId()));

        FunctionModel foundFunctionModel = this.functionRepository.findById(foundFieldModel.getFunction().getId())
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "No function exist for id: " + foundFieldModel.getFunction().getId()));

        TaskModel foundTaskModel = this.taskRepository.findById(foundFunctionModel.getTask().getId()).orElseThrow(
                () -> new IllegalArgumentException("No task exist for id: " + foundFunctionModel.getTask().getId()));

        TaskPrototypeModel foundTaskPrototypeModel = this.taskPrototypeRepository
                .findById(foundTaskModel.getTaskPrototype().getId()).orElseThrow(
                        () -> new IllegalArgumentException(
                                "No task_prototype exist for id: " + foundFunctionModel.getTask().getId()));

        // Create the directory path
        String directoryPath = "../data/" + foundTaskPrototypeModel.getTaskType() + "/" +
                "TASK-" + foundTaskModel.getId() + "/" +
                "FUNCTION-" + foundFunctionModel.getId() + "/" +
                "FIELD-" + foundFieldModel.getId() + "/" +
                "COLUMN-" + columnModel.getId();

        return directoryPath;
    }

    private ColumnDto columnModelToDto(ColumnModel columnModel) {
        if (columnModel == null) {
            return null;
        }
        ColumnDto columnDto = this.modelMapper.map(columnModel, ColumnDto.class);
        columnDto.setColumnPrototypeId(columnDto.getColumnPrototypeId());
        columnDto.setFieldId(columnDto.getFieldId());

        if (columnModel.getColumnPrototype().getColumnType().equals(ColumnType.FILE.name())) { // TODO: Retrieve the
                                                                                               // file name if exist
            // Create the directory path
            String directoryPath = this.getFilePath(columnModel);

            // Retrieve file names
            File directory = new File(directoryPath);
            File[] files = directory.listFiles();

            if (files != null && files.length > 0) {
                List<String> filePaths = Arrays.stream(files)
                        .map(File::getAbsolutePath)
                        .collect(Collectors.toList());

                // Set file paths in DTO
                columnDto.setFileDirectoryPaths(filePaths);
            } else {
                // If no files, set an empty list
                columnDto.setFileDirectoryPaths(Collections.emptyList());
            }
        }

        return columnDto;
    }

    private List<ColumnDto> columnModelsToDtos(List<ColumnModel> columnModels) {
        List<ColumnDto> columnDtos = new ArrayList<>();

        if (columnModels != null) {
            for (ColumnModel columnModel : columnModels) {
                columnDtos.add(this.columnModelToDto(columnModel));
            }
        }

        return columnDtos;
    }
}
