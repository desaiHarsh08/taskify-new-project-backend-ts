package com.taskify.services;

import com.taskify.dtos.FieldDto;
import com.taskify.dtos.FileDto;
import com.taskify.utils.PageResponse;

import java.util.Date;
import java.util.List;

public interface FieldServices {

    FieldDto createField(FieldDto field);

    PageResponse<FieldDto> getAllFields(int pageNumber);

    PageResponse<FieldDto> getFieldsByFieldPrototypeId(int pageNumber, Long fieldPrototypeId);

    List<FieldDto> getFieldsByFunctionId(Long functionId);

    FieldDto getFieldById(Long id);

    PageResponse<FieldDto> getFieldsByCreatedByUserId(int pageNumber, Long createdByUserId);

    PageResponse<FieldDto> getFieldsByCreatedDate(int pageNumber, Date createdDate);

    PageResponse<FieldDto> getFieldsByClosedByUserId(int pageNumber, Long closedByUserId);

    PageResponse<FieldDto> getFieldsByClosedDate(int pageNumber, Date closedDate);

    PageResponse<FieldDto> getFieldsByIsClosed(int pageNumber, boolean isClosed);

    FieldDto closeField(FieldDto givenField, Long userId);

    boolean deleteField(Long id, Long userId);

}
