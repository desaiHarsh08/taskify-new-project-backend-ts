package com.taskify.controllers;

import com.taskify.dtos.prototypes.FunctionPrototypeDto;
import com.taskify.services.FunctionPrototypeServices;
import com.taskify.utils.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/function-prototypes")
public class FunctionPrototypeController {

    @Autowired
    private FunctionPrototypeServices functionPrototypeServices;

    @PostMapping("")
    ResponseEntity<?> createFunctionPrototype(FunctionPrototypeDto functionPrototypeDto) {
        FunctionPrototypeDto createdFunctionPrototypeDto = this.functionPrototypeServices.createFunctionPrototype(functionPrototypeDto);

        if (createdFunctionPrototypeDto != null) {
            return new ResponseEntity<>(
                    createdFunctionPrototypeDto,
                    HttpStatus.CREATED
            );
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY, "Unable to create the function_prototype"),
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    @GetMapping("")
    ResponseEntity<?> getAllFunctionPrototypes(@RequestParam(name = "page") int pageNumber) {
        return new ResponseEntity<>(
                this.functionPrototypeServices.getAllFunctionPrototypes(pageNumber),
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    @GetMapping("/department/{department}")
    ResponseEntity<?> getFunctionPrototypesByDepartment(@PathVariable String department) {
        return new ResponseEntity<>(
                this.functionPrototypeServices.getFunctionPrototypesByDepartment(department),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getFunctionPrototypeById(@PathVariable Long id) {
        return new ResponseEntity<>(
                this.functionPrototypeServices.getFunctionPrototypeById(id),
                HttpStatus.OK
        );
    }

    @GetMapping("/title/{title}")
    ResponseEntity<?> getFunctionPrototypeByTitle(@PathVariable String title) {
        return new ResponseEntity<>(
                this.functionPrototypeServices.getFunctionPrototypeByTitle(title),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateFunctionPrototype(@PathVariable Long id, @RequestBody FunctionPrototypeDto givenFunctionPrototypeDto) {
        if (!id.equals(givenFunctionPrototypeDto.getId())) {
            throw new IllegalArgumentException("Invalid id provided!");
        }

        return new ResponseEntity<>(
                this.functionPrototypeServices.updateFunctionPrototype(givenFunctionPrototypeDto),
                HttpStatus.OK
        );
    }

    ResponseEntity<?> deleteFunctionPrototype(Long id) {
        return new ResponseEntity<>(
                this.functionPrototypeServices.deleteFunctionPrototype(id),
                HttpStatus.OK
        );
    }

}
