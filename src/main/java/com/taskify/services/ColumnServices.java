package com.taskify.services;

import com.taskify.dtos.ColumnDto;
import com.taskify.dtos.FileDto;
import com.taskify.utils.PageResponse;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ColumnServices {

    ColumnDto createColumn(ColumnDto column);

    boolean uploadFiles(ColumnDto columnDto, MultipartFile[] files);

    byte[] readFileAsBytes(String filePath);

    boolean deleteFile(String filePath);

    PageResponse<ColumnDto> getAllColumns(int pageNumber);

    ColumnDto getColumnById(Long id);

    PageResponse<ColumnDto> getColumnsByColumnPrototypeId(int pageNumber, Long columnPrototypeId);

    List<ColumnDto> getColumnsByFieldId(Long fieldId);

    ColumnDto updateColumn(ColumnDto givenColumn);

    boolean deleteColumn(Long id);

}
