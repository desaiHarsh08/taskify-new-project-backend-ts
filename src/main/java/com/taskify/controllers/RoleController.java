package com.taskify.controllers;

import com.taskify.dtos.RoleDto;
import com.taskify.services.RoleServices;
import com.taskify.utils.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleServices roleServices;

    @PostMapping("")
    public ResponseEntity<?> createRole(@RequestBody RoleDto roleDto) {
        RoleDto createdRoleDto = this.roleServices.createRole(roleDto);
        if (createdRoleDto != null) {
            return new ResponseEntity<>(createdRoleDto, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY, "Unable to create role"), HttpStatus.UNPROCESSABLE_ENTITY);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        return new ResponseEntity<>(this.roleServices.getRoleById(id), HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RoleDto>> getRolesByUserId(@PathVariable Long userId) {
        return new ResponseEntity<>(this.roleServices.getRolesByUserId(userId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        return new ResponseEntity<>(this.roleServices.deleteRole(id), HttpStatus.OK);
    }

}
