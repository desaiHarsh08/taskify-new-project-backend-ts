package com.taskify.utils;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ErrorMessage {

    private int statusCode;

    private HttpStatus httpStatus;
    
    private String message;

}