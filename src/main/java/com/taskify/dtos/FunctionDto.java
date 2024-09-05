package com.taskify.dtos;

import com.taskify.constants.Department;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDto {

    private Long id;

    private Long functionPrototypeId;

    private Long taskId;

    private String department = Department.QUOTATION.name();

    private Long createdByUserId;

    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime dueDate = LocalDateTime.now();

    private Long closedByUserId;

    private LocalDateTime closedDate;

    private Boolean isClosed = false;

    List<FieldDto> fields = new ArrayList<>();

}
