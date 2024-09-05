package com.taskify.utils;

import java.util.ArrayList;
import java.util.List;

import com.taskify.dtos.FileDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDtoListWrapper {

    private List<FileDto> fileDtos = new ArrayList<>();
    
}