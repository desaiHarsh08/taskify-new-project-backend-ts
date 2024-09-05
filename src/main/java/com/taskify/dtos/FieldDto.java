package com.taskify.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FieldDto {

    private Long id;

    private Long fieldPrototypeId;

    private Long functionId;

    private Long createdByUserId;

    private LocalDateTime createdDate = LocalDateTime.now();

    private Long closedByUserId;

    private LocalDateTime closedDate;

    private Boolean isClosed = false;

    List<ColumnDto> columns;

    private LocalDateTime lastEdited = LocalDateTime.now();

}
