package com.taskify.controllers;

import com.taskify.constants.ActionType;
import com.taskify.constants.ResourceType;
import com.taskify.dtos.TaskDto;
import com.taskify.services.TaskServices;
import com.taskify.utils.ErrorMessage;
import com.taskify.utils.MonthlyStats;
import com.taskify.utils.NotificationMessage;
import com.taskify.utils.PageResponse;
import com.taskify.utils.TaskStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private TaskServices taskServices;

    @PostMapping("")
    public ResponseEntity<?> createTask(@RequestBody TaskDto taskDto) {
        // System.out.println("TaskDto: " + taskDto);
        TaskDto createdTaskDto = this.taskServices.createTask(taskDto);

        if (createdTaskDto != null) {
            // Notify all users
            this.messagingTemplate.convertAndSend("/topic/return-to", new NotificationMessage<TaskDto>(
                    ActionType.CREATE.name(),
                    ResourceType.TASK.name(),
                    createdTaskDto));
            return new ResponseEntity<>(createdTaskDto, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to create the task"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping("")
    public ResponseEntity<PageResponse<TaskDto>> getAllTasks(@RequestParam(name = "page") int pageNumber) {
        return new ResponseEntity<>(this.taskServices.getAllTasks(pageNumber), HttpStatus.OK);
    }

    @GetMapping("/task-prototypes/{taskPrototypeId}")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByPrototypeId(@RequestParam(name = "page") int pageNumber,
            @PathVariable Long taskPrototypeId) {
        return new ResponseEntity<>(this.taskServices.getTasksByPrototypeId(pageNumber, taskPrototypeId),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        return new ResponseEntity<>(this.taskServices.getTasksById(id), HttpStatus.OK);
    }

    @GetMapping("/abbreviation/{taskAbbreviation}")
    public ResponseEntity<?> getTaskByAbbreviation(@PathVariable String taskAbbreviation) {
        return new ResponseEntity<>(this.taskServices.getTaskByAbbreviation(taskAbbreviation), HttpStatus.OK);
    }

    @GetMapping("/priority/{priorityType}")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByPriority(@RequestParam(name = "page") int pageNumber,
            @PathVariable String priorityType) {
        return new ResponseEntity<>(this.taskServices.getTasksByPriority(pageNumber, priorityType), HttpStatus.OK);
    }

    @GetMapping("/created-by")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByCreatedUser(@RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "createdBy") Long createdByUserId) {
        return new ResponseEntity<>(this.taskServices.getTasksByCreatedUser(pageNumber, createdByUserId),
                HttpStatus.OK);
    }

    @GetMapping("/assigned-to")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByAssignedUser(@RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "assignedTo") Long assignedByUserId) {
        return new ResponseEntity<>(this.taskServices.getTasksByAssignedUser(pageNumber, assignedByUserId),
                HttpStatus.OK);
    }

    @GetMapping("/abbreviation-date")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByAbbreviationAndCreatedDate(
            @RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "abbreviation") String taskAbbreviation, @RequestParam(name = "date") LocalDate date) {
        return new ResponseEntity<>(
                this.taskServices.getTaskByAbbreviationOrCreatedDate(pageNumber, taskAbbreviation, date),
                HttpStatus.OK);
    }

    @GetMapping("/overdue")
    public ResponseEntity<PageResponse<TaskDto>> getOverdueTasks(@RequestParam(name = "page") int pageNumber) {
        return new ResponseEntity<>(this.taskServices.getOverdueTasks(pageNumber),
                HttpStatus.OK);
    }

    @GetMapping("/created-date")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByCreatedDate(@RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "date") Date createdDate) {
        return new ResponseEntity<>(this.taskServices.getTasksByCreatedDate(pageNumber, createdDate), HttpStatus.OK);
    }

    @GetMapping("/closed-date")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByClosedDate(@RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "date") Date closedDate) {
        return new ResponseEntity<>(this.taskServices.getTasksByClosedDate(pageNumber, closedDate), HttpStatus.OK);
    }

    @GetMapping("/is-closed")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByIsClosed(@RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "isClosed") boolean isClosed) {
        return new ResponseEntity<>(this.taskServices.getTasksByIsClosed(pageNumber, isClosed), HttpStatus.OK);
    }

    @GetMapping("/closed-by")
    public ResponseEntity<PageResponse<TaskDto>> getTasksByClosedByUserId(@RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "closedBy") Long closedByUserId) {
        return new ResponseEntity<>(this.taskServices.getTasksByClosedByUserId(pageNumber, closedByUserId),
                HttpStatus.OK);
    }

    @PostMapping("/do-close")
    public ResponseEntity<TaskDto> doCloseTask(@RequestBody TaskDto taskDto) {
        return new ResponseEntity<>(this.taskServices.closeTask(taskDto), HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<TaskStats> getStats() {
        return new ResponseEntity<>(this.taskServices.totalStats(), HttpStatus.OK);
    }

    @GetMapping("/monthly-stats")
    public ResponseEntity<MonthlyStats> getMonthlyStats() {
        return new ResponseEntity<>(this.taskServices.monthlyStats(), HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TaskDto>> getTasksByCustomer(@PathVariable Long customerId) {
        return new ResponseEntity<>(this.taskServices.getTasksByCustomer(customerId), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskDto givenTaskDto,
            @RequestParam Long userId) {
        if (!id.equals(givenTaskDto.getId())) {
            return new ResponseEntity<>(
                    new ErrorMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "Invalid id provided!"),
                    HttpStatus.BAD_REQUEST);
        }

        TaskDto updatedTaskDto = this.taskServices.updateTask(givenTaskDto, userId);

        if (updatedTaskDto != null) {
            // Notify all users
            this.messagingTemplate.convertAndSend("/topic/return-to", new NotificationMessage<TaskDto>(
                    ActionType.UPDATE.name(),
                    ResourceType.TASK.name(),
                    updatedTaskDto));

            return new ResponseEntity<>(updatedTaskDto, HttpStatus.OK);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to update the task"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, @RequestParam Long userId) {
        boolean isDeleted = this.taskServices.deleteTask(id, userId);

        if (isDeleted) {
            // Notify all users
            this.messagingTemplate.convertAndSend("/topic/return-to", new NotificationMessage<Long>(
                    ActionType.DELETE.name(),
                    ResourceType.TASK.name(),
                    id));
            return new ResponseEntity<>(
                    new ErrorMessage(HttpStatus.OK.value(), HttpStatus.OK, "Task deleted successfully"), HttpStatus.OK);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to delete the task"),
                HttpStatus.NOT_FOUND);
    }
}
