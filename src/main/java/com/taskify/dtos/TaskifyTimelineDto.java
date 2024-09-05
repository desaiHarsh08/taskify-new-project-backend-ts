package com.taskify.dtos;

import com.taskify.constants.ActionType;
import com.taskify.constants.ResourceType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskifyTimelineDto {

    private Long id;

    private Long userId;

    private String resourceType = ResourceType.TASK.name();

    private String actionType = ActionType.CREATE.name();

    private LocalDateTime atDate;

    private String taskAbbreviation;

    private String functionName;

    private String fieldName;

    private String customerName;
}
