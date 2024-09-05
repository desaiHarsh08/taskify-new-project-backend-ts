package com.taskify.dtos;

import com.taskify.constants.PriorityType;
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
public class TaskDto {

    private Long id;

    private Long taskPrototypeId;

    private String priorityType = PriorityType.NORMAL.name();

    private Long createdByUserId;

    private Long assignedToUserId;

    private LocalDateTime createdDate = LocalDateTime.now();

    private Long closedByUserId;

    private LocalDateTime closedDate;

    private Boolean isClosed = false;

    private Long customerId;

    private List<FunctionDto> functions = new ArrayList<>();

    private String taskAbbreviation;

    private String pumpType;

    private String pumpManufacturer;

    private String specifications;

    private String requirements;

    private String problemDescription;

}
