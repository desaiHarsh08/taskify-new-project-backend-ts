package com.taskify.controllers;

import com.taskify.dtos.TaskifyTimelineDto;
import com.taskify.models.TaskifyTimelineModel;
import com.taskify.services.TaskifyTimelineServices;
import com.taskify.utils.ErrorMessage;
import com.taskify.utils.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RestController
@RequestMapping("/api/taskify-timelines")
public class TaskifyTimelineController {

    @Autowired
    private TaskifyTimelineServices taskifyTimelineServices;

    @PostMapping("")
    public ResponseEntity<?> createTaskifyTimeline(@RequestBody TaskifyTimelineDto taskifyTimelineDto) {
        TaskifyTimelineDto createdTimelineDto = taskifyTimelineServices.createTaskifyTimeline(taskifyTimelineDto);
        if (createdTimelineDto != null) {
            return new ResponseEntity<>(createdTimelineDto, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to create the taskify_timeline"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping("")
    public ResponseEntity<PageResponse<TaskifyTimelineDto>> getAllTaskifyTimelines(
            @RequestParam(name = "page") int pageNumber) {
        return new ResponseEntity<>(taskifyTimelineServices.getAllTaskifyTimelines(pageNumber), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponse<TaskifyTimelineDto>> getUserHistory(@RequestParam(name = "page") int pageNumber,
            @PathVariable Long userId) {
        return new ResponseEntity<>(taskifyTimelineServices.getUserHistory(pageNumber, userId), HttpStatus.OK);
    }

    @GetMapping("/resource/{resourceType}")
    public ResponseEntity<PageResponse<TaskifyTimelineDto>> getTaskifyTimelinesByResourceType(
            @RequestParam(name = "page") int pageNumber, @PathVariable String resourceType) {
        return new ResponseEntity<>(taskifyTimelineServices.getTaskifyTimelinesByResourceType(pageNumber, resourceType),
                HttpStatus.OK);
    }

    @GetMapping("/action/{actionType}")
    public ResponseEntity<PageResponse<TaskifyTimelineDto>> getTaskifyTimelinesByActionType(
            @RequestParam(name = "page") int pageNumber, @PathVariable String actionType) {
        return new ResponseEntity<>(taskifyTimelineServices.getTaskifyTimelinesByActionType(pageNumber, actionType),
                HttpStatus.OK);
    }

    @GetMapping("/date")
    public ResponseEntity<PageResponse<TaskifyTimelineDto>> getTaskifyTimelinesByAtDate(
            @RequestParam(name = "page") int pageNumber, @RequestParam(name = "date") String date) {
        // return new
        // ResponseEntity<>(taskifyTimelineServices.getTaskifyTimelinesByAtDate(pageNumber,
        // new Date(atDate)),
        // HttpStatus.OK);

        // SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // Date parsedDate;

        // try {
        // parsedDate = dateFormat.parse(atDate);
        // } catch (ParseException e) {
        // // Handle the exception, return a bad request response, or handle it in
        // another way
        // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        // }

        // return new
        // ResponseEntity<>(taskifyTimelineServices.getTaskifyTimelinesByAtDate(pageNumber,
        // parsedDate),
        // HttpStatus.OK);

        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        // LocalDateTime localDateTime = LocalDate.parse(atDate,
        // formatter).atStartOfDay();
        // return new
        // ResponseEntity<>(taskifyTimelineServices.getTaskifyTimelinesByAtDate(pageNumber,
        // localDateTime),
        // HttpStatus.OK);

        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return new ResponseEntity<>(
            taskifyTimelineServices.getTaskifyTimelinesByAtDate(pageNumber, localDate), HttpStatus.OK);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<PageResponse<TaskifyTimelineDto>> getTaskifyTimelinesByTaskId(
            @RequestParam(name = "page") int pageNumber, @PathVariable String taskAbbreviation) {
        return new ResponseEntity<>(
                taskifyTimelineServices.getTaskifyTimelinesByTaskAbbreviation(pageNumber, taskAbbreviation),
                HttpStatus.OK);
    }

    @GetMapping("/function/{functionId}")
    public ResponseEntity<PageResponse<TaskifyTimelineDto>> getTaskifyTimelinesByFunctionId(
            @RequestParam(name = "page") int pageNumber, @PathVariable String functionName) {
        return new ResponseEntity<>(taskifyTimelineServices.getTaskifyTimelinesByFunctionName(pageNumber, functionName),
                HttpStatus.OK);
    }

    // @GetMapping("/field/{fieldId}")
    // public ResponseEntity<PageResponse<TaskifyTimelineDto>>
    // getTaskifyTimelinesByFieldId(@RequestParam(name = "page") int pageNumber,
    // @PathVariable Long fieldId) {
    // return new
    // ResponseEntity<>(taskifyTimelineServices.getTaskifyTimelinesByFieldId(pageNumber,
    // fieldId), HttpStatus.OK);
    // }

}
