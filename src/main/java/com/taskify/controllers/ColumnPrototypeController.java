package com.taskify.controllers;

import com.taskify.dtos.prototypes.ColumnPrototypeDto;
import com.taskify.services.ColumnPrototypeServices;
import com.taskify.utils.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/column-prototypes")
public class ColumnPrototypeController {

    @Autowired
    private ColumnPrototypeServices columnPrototypeServices;

    @PostMapping("")
    public ResponseEntity<?> createColumnPrototype(@RequestBody ColumnPrototypeDto columnPrototypeDto) {
        ColumnPrototypeDto createdColumnPrototypeDto = this.columnPrototypeServices
                .createColumnPrototype(columnPrototypeDto);

        if (createdColumnPrototypeDto != null) {
            return new ResponseEntity<>(
                    createdColumnPrototypeDto,
                    HttpStatus.CREATED);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to create the column prototype"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getColumnPrototypeById(@PathVariable Long id) {
        return new ResponseEntity<>(
                this.columnPrototypeServices.getColumnPrototypeById(id),
                HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateColumnPrototype(@PathVariable Long id,
            @RequestBody ColumnPrototypeDto givenColumnPrototypeDto) {
        if (!id.equals(givenColumnPrototypeDto.getId())) {
            return new ResponseEntity<>(
                    new ErrorMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "Invalid id provided!"),
                    HttpStatus.BAD_REQUEST);
        }

        ColumnPrototypeDto updatedColumnPrototypeDto = this.columnPrototypeServices
                .updateColumnPrototype(givenColumnPrototypeDto);

        if (updatedColumnPrototypeDto != null) {
            return new ResponseEntity<>(updatedColumnPrototypeDto, HttpStatus.OK);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                        "Unable to update the column prototype"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteColumnPrototype(@PathVariable Long id) {
        return new ResponseEntity<>(
                this.columnPrototypeServices.deleteColumnPrototype(id),
                HttpStatus.OK);
    }

}
