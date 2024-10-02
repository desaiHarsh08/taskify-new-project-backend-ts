package com.taskify.controllers;

import com.taskify.constants.ActionType;
import com.taskify.constants.ResourceType;
import com.taskify.dtos.ColumnDto;
import com.taskify.dtos.FileDto;
import com.taskify.dtos.FunctionDto;
import com.taskify.services.FunctionServices;
import com.taskify.utils.ErrorMessage;
import com.taskify.utils.FileDtoListWrapper;
import com.taskify.utils.NotificationMessage;
import com.taskify.utils.PageResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/functions")
public class FunctionController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private FunctionServices functionServices;

    @PostMapping("")
    public ResponseEntity<?> createFunction(@RequestBody FunctionDto functionDto) {
        FunctionDto createdFunctionDto = this.functionServices.createFunction(functionDto);

        if (createdFunctionDto != null) {
            // Notify all users
            this.messagingTemplate.convertAndSend("/topic/return-to", new NotificationMessage<FunctionDto>(
                    ActionType.CREATE.name(),
                    ResourceType.FUNCTION.name(),
                    createdFunctionDto));
            return new ResponseEntity<>(createdFunctionDto, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to create the function"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping("")
    public ResponseEntity<PageResponse<FunctionDto>> getAllFunctions(@RequestParam(name = "page") int pageNumber) {
        return new ResponseEntity<>(this.functionServices.getAllFunctions(pageNumber), HttpStatus.OK);
    }

    @PostMapping(value = "/upload-files", consumes = { "multipart/form-data" })
    public ResponseEntity<?> uploadFiles(
            @RequestPart("function") FunctionDto functionDto,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        return new ResponseEntity<>(
                this.functionServices.uploadFiles(functionDto, files),
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


    @GetMapping("/{id}")
    public ResponseEntity<?> getFunctionById(@PathVariable Long id) {
        return new ResponseEntity<>(this.functionServices.getFunctionById(id), HttpStatus.OK);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<FunctionDto>> getFunctionsByTaskId(@PathVariable Long taskId) {
        List<FunctionDto> functions = this.functionServices.getFunctionsByTaskId(taskId);
        return new ResponseEntity<>(functions, HttpStatus.OK);
    }

    @GetMapping("/department")
    public ResponseEntity<PageResponse<FunctionDto>> getFunctionsByDepartment(
            @RequestParam(name = "page") int pageNumber, @RequestParam(name = "department") String department) {
        return new ResponseEntity<>(this.functionServices.getFunctionsByDepartment(pageNumber, department),
                HttpStatus.OK);
    }

    @GetMapping("/created-by")
    public ResponseEntity<PageResponse<FunctionDto>> getFunctionsByCreatedByUser(
            @RequestParam(name = "page") int pageNumber, @RequestParam(name = "createdBy") Long createdByUserId) {
        return new ResponseEntity<>(this.functionServices.getFunctionsByCreatedByUser(pageNumber, createdByUserId),
                HttpStatus.OK);
    }

    @GetMapping("/created-date")
    public ResponseEntity<PageResponse<FunctionDto>> getFunctionsByCreatedDate(
            @RequestParam(name = "page") int pageNumber, @RequestParam(name = "date") Date createdDate) {
        return new ResponseEntity<>(this.functionServices.getFunctionsByCreatedDate(pageNumber, createdDate),
                HttpStatus.OK);
    }

    @GetMapping("/due-date")
    public ResponseEntity<PageResponse<FunctionDto>> getFunctionsByDueDate(@RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "date") Date dueDate) {
        return new ResponseEntity<>(this.functionServices.getFunctionsByDueDate(pageNumber, dueDate), HttpStatus.OK);
    }

    @GetMapping("/closed-by")
    public ResponseEntity<PageResponse<FunctionDto>> getFunctionsByClosedByUserId(
            @RequestParam(name = "page") int pageNumber, @RequestParam(name = "closedBy") Long closedByUserId) {
        return new ResponseEntity<>(this.functionServices.getFunctionsByClosedByUserId(pageNumber, closedByUserId),
                HttpStatus.OK);
    }

    @GetMapping("/closed-date")
    public ResponseEntity<PageResponse<FunctionDto>> getFunctionsByClosedDate(
            @RequestParam(name = "page") int pageNumber, @RequestParam(name = "date") Date closedDate) {
        return new ResponseEntity<>(this.functionServices.getFunctionsByClosedDate(pageNumber, closedDate),
                HttpStatus.OK);
    }

    @GetMapping("/is-closed")
    public ResponseEntity<PageResponse<FunctionDto>> getFunctionsByIsClosed(@RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "isClosed") boolean isClosed) {
        return new ResponseEntity<>(this.functionServices.getFunctionsByIsClosed(pageNumber, isClosed), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFunction(@PathVariable Long id, @RequestBody FunctionDto givenFunctionDto,
            @RequestParam Long userId) {
        if (!id.equals(givenFunctionDto.getId())) {
            return new ResponseEntity<>(
                    new ErrorMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "Invalid id provided!"),
                    HttpStatus.BAD_REQUEST);
        }

        FunctionDto updatedFunctionDto = this.functionServices.updateFunction(givenFunctionDto, userId);

        if (updatedFunctionDto != null) {
            // Notify all users
            this.messagingTemplate.convertAndSend("/topic/return-to", new NotificationMessage<FunctionDto>(
                    ActionType.UPDATE.name(),
                    ResourceType.FUNCTION.name(),
                    updatedFunctionDto));
            return new ResponseEntity<>(updatedFunctionDto, HttpStatus.OK);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to update the function"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @PutMapping("/do-close/{id}")
    public ResponseEntity<?> closeFunction(@PathVariable Long id, @RequestBody FunctionDto functionDto,
            @RequestParam Long userId) {

        return new ResponseEntity<>(
                this.functionServices.closeFunction(functionDto, userId),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFunction(@PathVariable Long id, @RequestParam Long userId) {
        boolean isDeleted = this.functionServices.deleteFunction(id, userId);

        if (isDeleted) {
            // Notify all users
            this.messagingTemplate.convertAndSend("/topic/return-to", new NotificationMessage<Long>(
                    ActionType.DELETE.name(),
                    ResourceType.FUNCTION.name(),
                    id));

            return new ResponseEntity<>(
                    new ErrorMessage(HttpStatus.OK.value(), HttpStatus.OK, "Function deleted successfully"),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to delete the function"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
