package com.taskify.controllers;

import com.taskify.dtos.prototypes.FieldPrototypeDto;
import com.taskify.services.FieldPrototypeServices;
import com.taskify.utils.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/field-prototypes")
public class FieldPrototypeController {

    @Autowired
    private FieldPrototypeServices fieldPrototypeServices;

    @PostMapping("")
    public ResponseEntity<?> createFieldPrototype(@RequestBody FieldPrototypeDto fieldPrototypeDto) {
        FieldPrototypeDto createdFieldPrototypeDto = this.fieldPrototypeServices
                .createFieldPrototype(fieldPrototypeDto);

        if (createdFieldPrototypeDto != null) {
            return new ResponseEntity<>(
                    createdFieldPrototypeDto,
                    HttpStatus.CREATED);
        }

        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY, "Unable to create the field prototype"),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFieldPrototypeById(@PathVariable Long id) {
        FieldPrototypeDto fieldPrototypeDto = this.fieldPrototypeServices.getFieldPrototypeById(id);

        return new ResponseEntity<>(fieldPrototypeDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFieldPrototype(@PathVariable Long id,
            @RequestBody FieldPrototypeDto givenFieldPrototypeDto) {
        if (!id.equals(givenFieldPrototypeDto.getId())) {
            return new ResponseEntity<>(
                    new ErrorMessage(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "Invalid id provided!"),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                this.fieldPrototypeServices.updateFieldPrototype(givenFieldPrototypeDto),
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFieldPrototype(@PathVariable Long id) {
        return new ResponseEntity<>(
                new ErrorMessage(HttpStatus.OK.value(), HttpStatus.OK, "Field prototype deleted successfully"),
                HttpStatus.OK);
    }

}
