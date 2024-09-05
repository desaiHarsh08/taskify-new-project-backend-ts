package com.taskify.controllers;

import com.taskify.models.RefreshTokenModel;
import com.taskify.services.RefreshTokenServices;
import com.taskify.utils.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refresh")
public class RefreshTokenController {

    @Autowired
    private RefreshTokenServices refreshTokenServices;

    @PostMapping("/create")
    public ResponseEntity<?> createRefreshToken(@RequestParam String email) {
        try {
            RefreshTokenModel refreshToken = refreshTokenServices.createRefreshToken(email);
            return new ResponseEntity<>(refreshToken, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ErrorMessage(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY, "Unable to create refresh token"),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

}
