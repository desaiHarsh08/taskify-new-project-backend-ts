package com.taskify.controllers;

import com.taskify.dtos.ColumnDto;
import com.taskify.dtos.FileDto;
import com.taskify.services.ColumnServices;
import com.taskify.utils.ErrorMessage;
import com.taskify.utils.FileDtoListWrapper;
import com.taskify.utils.PageResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/columns")
public class ColumnController {

    @Autowired
    private ColumnServices columnServices;

    @PostMapping("")
    public ResponseEntity<?> createColumn(@RequestBody ColumnDto columnDto) {
        // Create column
        ColumnDto createdColumn = columnServices.createColumn(columnDto);

        if (createdColumn != null) {
            return new ResponseEntity<>(createdColumn, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to create the column"),
                HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getColumnById(@PathVariable Long id) {
        ColumnDto columnDto = columnServices.getColumnById(id);
        return new ResponseEntity<>(columnDto, HttpStatus.OK);
    }

    @PostMapping(value = "/upload-files", consumes = { "multipart/form-data" })
    public ResponseEntity<?> uploadFiles(
            @RequestPart("column") ColumnDto columnDto,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        return new ResponseEntity<>(
                this.columnServices.uploadFiles(columnDto, files),
                HttpStatus.OK);
    }

    @GetMapping("/get-files")
    public ResponseEntity<byte[]> getFile(@RequestParam String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] fileContent = inputStream.readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            String mimeType = Files.probeContentType(file.toPath()); // Automatically determine MIME type
            if (mimeType == null) {
                mimeType = "application/octet-stream"; // Default to binary if MIME type can't be determined
            }
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentDisposition(ContentDisposition.builder("inline").filename(file.getName()).build());

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateColumn(@PathVariable Long id, @RequestBody ColumnDto columnDto) {
        if (!id.equals(columnDto.getId())) {
            throw new IllegalArgumentException("Invalid id provided!");
        }
        ColumnDto updatedColumn = this.columnServices.updateColumn(columnDto);
        if (updatedColumn != null) {
            return new ResponseEntity<>(updatedColumn, HttpStatus.OK);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to update the column"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteColumn(@PathVariable Long id) {
        return new ResponseEntity<>(columnServices.deleteColumn(id), HttpStatus.OK);
    }

    @GetMapping("/by-prototype/{columnPrototypeId}")
    public ResponseEntity<PageResponse<ColumnDto>> getColumnsByColumnPrototypeId(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @PathVariable Long columnPrototypeId) {
        return new ResponseEntity<>(
                columnServices.getColumnsByColumnPrototypeId(pageNumber, columnPrototypeId),
                HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/by-field/{fieldId}")
    public ResponseEntity<?> getColumnsByFieldId(@PathVariable Long fieldId) {
        return new ResponseEntity<>(this.columnServices.getColumnsByFieldId(fieldId), HttpStatus.OK);
    }

}
