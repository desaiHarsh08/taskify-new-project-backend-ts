package com.taskify.models;

import com.taskify.constants.ActionType;
import com.taskify.constants.ModelConstants;
import com.taskify.constants.ResourceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = ModelConstants.TASK_TIMELINE_TABLE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskifyTimelineModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "user_id_fk")
    private UserModel user;

    private String resourceType = ResourceType.TASK.name();

    private String actionType = ActionType.CREATE.name();

    private LocalDateTime atDate = LocalDateTime.now();

    private String taskAbbreviation;

    private String functionName;

    private String fieldName;

    private String customerName;

}
