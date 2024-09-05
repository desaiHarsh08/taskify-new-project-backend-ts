package com.taskify.dtos;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {

    private MultipartFile file;

    private Long columnPrototypeId;

    private Long fieldPrototypeId;

    private Long functionPrototypeId;

    private Long taskPrototypeId;

    private Long taskId;

}
