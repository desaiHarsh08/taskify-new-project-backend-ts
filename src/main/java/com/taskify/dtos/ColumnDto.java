package com.taskify.dtos;

import lombok.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColumnDto {

    private Long id;

    private Long columnPrototypeId;

    private Long createdByUserId;

    private Long fieldId;

    private Long numberValue;

    private String textValue;

    private Boolean booleanValue = false;

    private Date dateValue;

    private List<String> fileDirectoryPaths = new ArrayList<>();

}
