package com.taskify.controllers;

import com.taskify.dtos.prototypes.FunctionPrototypeDto;
import com.taskify.dtos.prototypes.TaskPrototypeDto;
import com.taskify.services.TaskPrototypeServices;
import com.taskify.utils.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task-prototypes")
public class TaskPrototypeController {

    @Autowired
    private TaskPrototypeServices taskPrototypeServices;

    @PostMapping("")
    ResponseEntity<?> createTaskPrototype(@RequestBody TaskPrototypeDto taskPrototypeDto) {
        System.out.println(taskPrototypeDto);
        TaskPrototypeDto createdTaskPrototypeDto = this.taskPrototypeServices.createTaskPrototype(taskPrototypeDto);
        if (createdTaskPrototypeDto != null) {
            return new ResponseEntity<>(
                    createdTaskPrototypeDto,
                    HttpStatus.CREATED);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to create task_prototype, may be it is already exist!"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @PostMapping("/add-function-prototype/{taskPrototypeId}")
    ResponseEntity<?> addFunctionPrototypes(@PathVariable Long taskPrototypeId,
            @RequestBody FunctionPrototypeDto functionPrototypeDto) {
        System.out.println(functionPrototypeDto);
        TaskPrototypeDto taskPrototypeDto = this.taskPrototypeServices
                .addFunctionPrototypeToTaskPrototype(taskPrototypeId, functionPrototypeDto);
        if (taskPrototypeDto != null) {
            return new ResponseEntity<>(
                    taskPrototypeDto,
                    HttpStatus.CREATED);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to add function_prototype to task_prototype!"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping("")
    ResponseEntity<?> getAllTaskPrototypes(@RequestParam(name = "page") int pageNumber) {
        return new ResponseEntity<>(
                this.taskPrototypeServices.getAllTaskPrototypes(pageNumber),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getTaskPrototypeById(@PathVariable Long id) {
        return new ResponseEntity<>(
                this.taskPrototypeServices.getTaskPrototypeById(id),
                HttpStatus.OK);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateTaskPrototype(@PathVariable Long id, @RequestBody TaskPrototypeDto givenTaskPrototype,
            @RequestParam Long userId) {
        if (!id.equals(givenTaskPrototype.getId())) {
            throw new IllegalArgumentException("Invalid task_id provided!");
        }

        return new ResponseEntity<>(
                this.taskPrototypeServices.updateTaskPrototype(givenTaskPrototype, userId),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteTaskPrototype(@PathVariable Long id, @RequestParam Long userId) {
        return new ResponseEntity<>(
                this.taskPrototypeServices.deleteTaskPrototype(id, userId),
                HttpStatus.OK);
    }

}
