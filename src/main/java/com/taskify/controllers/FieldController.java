package com.taskify.controllers;

import com.taskify.constants.ActionType;
import com.taskify.constants.ResourceType;
import com.taskify.dtos.FieldDto;
import com.taskify.dtos.FileDto;
import com.taskify.services.FieldServices;
import com.taskify.utils.ErrorMessage;
import com.taskify.utils.FileDtoListWrapper;
import com.taskify.utils.NotificationMessage;
import com.taskify.utils.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/fields")
public class FieldController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private FieldServices fieldServices;

    @PostMapping("")
    public ResponseEntity<?> createField(@RequestBody FieldDto fieldDto) {

        FieldDto createdFieldDto = this.fieldServices.createField(fieldDto);

        if (createdFieldDto != null) {
            // Notify all users
            this.messagingTemplate.convertAndSend("/topic/return-to", new NotificationMessage<FieldDto>(
                    ActionType.CREATE.name(),
                    ResourceType.FIELD.name(),
                    createdFieldDto));
            return new ResponseEntity<>(createdFieldDto, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to create the field"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping("")
    public ResponseEntity<PageResponse<FieldDto>> getAllFields(@RequestParam(name = "page") int pageNumber) {
        return new ResponseEntity<>(this.fieldServices.getAllFields(pageNumber), HttpStatus.OK);
    }

    @GetMapping("/prototype/{fieldPrototypeId}")
    public ResponseEntity<PageResponse<FieldDto>> getFieldsByFieldPrototypeId(
            @RequestParam(name = "page") int pageNumber, @PathVariable Long fieldPrototypeId) {
        return new ResponseEntity<>(this.fieldServices.getFieldsByFieldPrototypeId(pageNumber, fieldPrototypeId),
                HttpStatus.OK);
    }

    @GetMapping("/function/{functionId}")
    public ResponseEntity<List<FieldDto>> getFieldsByFunctionId(@PathVariable Long functionId) {
        List<FieldDto> fields = this.fieldServices.getFieldsByFunctionId(functionId);
        return new ResponseEntity<>(fields, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFieldById(@PathVariable Long id) {
        return new ResponseEntity<>(this.fieldServices.getFieldById(id), HttpStatus.OK);
    }

    @GetMapping("/created-by")
    public ResponseEntity<PageResponse<FieldDto>> getFieldsByCreatedByUserId(
            @RequestParam(name = "page") int pageNumber, @RequestParam(name = "createdBy") Long createdByUserId) {
        return new ResponseEntity<>(this.fieldServices.getFieldsByCreatedByUserId(pageNumber, createdByUserId),
                HttpStatus.OK);
    }

    @GetMapping("/created-date")
    public ResponseEntity<PageResponse<FieldDto>> getFieldsByCreatedDate(@RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "date") Date createdDate) {
        return new ResponseEntity<>(this.fieldServices.getFieldsByCreatedDate(pageNumber, createdDate), HttpStatus.OK);
    }

    @GetMapping("/closed-by")
    public ResponseEntity<PageResponse<FieldDto>> getFieldsByClosedByUserId(@RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "closedBy") Long closedByUserId) {
        return new ResponseEntity<>(this.fieldServices.getFieldsByClosedByUserId(pageNumber, closedByUserId),
                HttpStatus.OK);
    }

    @GetMapping("/closed-date")
    public ResponseEntity<PageResponse<FieldDto>> getFieldsByClosedDate(@RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "date") Date closedDate) {
        return new ResponseEntity<>(this.fieldServices.getFieldsByClosedDate(pageNumber, closedDate), HttpStatus.OK);
    }

    @GetMapping("/is-closed")
    public ResponseEntity<PageResponse<FieldDto>> getFieldsByIsClosed(@RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "isClosed") boolean isClosed) {
        return new ResponseEntity<>(this.fieldServices.getFieldsByIsClosed(pageNumber, isClosed), HttpStatus.OK);
    }

    @PutMapping("/close")
    public ResponseEntity<?> closeField(@RequestBody FieldDto fieldDto, @RequestParam Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Please provide a valid userId!");
        }
        System.out.println(userId);
        FieldDto closedFieldDto = this.fieldServices.closeField(fieldDto, userId);

        if (closedFieldDto != null) {
            // Notify all users
            this.messagingTemplate.convertAndSend("/topic/return-to", new NotificationMessage<FieldDto>(
                    ActionType.CLOSED.name(),
                    ResourceType.FIELD.name(),
                    closedFieldDto));
            return new ResponseEntity<>(closedFieldDto, HttpStatus.OK);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to close the field"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteField(@PathVariable Long id, @RequestParam Long userId) {
        boolean isDeleted = this.fieldServices.deleteField(id, userId);

        if (isDeleted) {
            // Notify all users
            this.messagingTemplate.convertAndSend("/topic/return-to", new NotificationMessage<Long>(
                    ActionType.DELETE.name(),
                    ResourceType.FIELD.name(),
                    id));

            return new ResponseEntity<>(
                    new ErrorMessage(HttpStatus.OK.value(), HttpStatus.OK, "Field deleted successfully"),
                    HttpStatus.OK);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to delete the field"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
