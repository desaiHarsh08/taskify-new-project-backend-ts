package com.taskify.controllers;

import com.taskify.dtos.UserDto;
import com.taskify.services.UserServices;
import com.taskify.utils.ErrorMessage;
import com.taskify.utils.PageResponse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServices userServices;

    @GetMapping("")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return new ResponseEntity<>(this.userServices.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return new ResponseEntity<>(this.userServices.getUserById(id), HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return new ResponseEntity<>(this.userServices.getUserByEmail(email), HttpStatus.OK);
    }

    @GetMapping("/department")
    public ResponseEntity<PageResponse<UserDto>> getUsersByDepartment(@RequestParam(name = "page") int pageNumber,
            @RequestParam(name = "department") String department) {
        return new ResponseEntity<>(this.userServices.getUsersByDepartment(pageNumber, department), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDto givenUserDto) {
        if (!id.equals(givenUserDto.getId())) {
            return new ResponseEntity<>(
                    new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY,
                            "Invalid id provided!"),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(this.userServices.updateUser(givenUserDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return new ResponseEntity<>(new ErrorMessage(HttpStatus.OK.value(), HttpStatus.OK, "User deleted successfully"),
                HttpStatus.OK);
    }

}
