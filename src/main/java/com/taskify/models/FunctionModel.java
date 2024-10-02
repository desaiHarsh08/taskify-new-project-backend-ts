package com.taskify.models;

import com.taskify.constants.Department;
import com.taskify.constants.ModelConstants;
import com.taskify.models.prototypes.FunctionPrototypeModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = ModelConstants.FUNCTION_TABLE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FunctionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = FunctionPrototypeModel.class)
    @JoinColumn(name = "function_prototype_id_fk")
    private FunctionPrototypeModel functionPrototype;

    @ManyToOne(targetEntity = TaskModel.class)
    @JoinColumn(name = "task_id_fk")
    private TaskModel task;

    private String department = Department.QUOTATION.name();

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "created_by_user_id_fk")
    private UserModel createdByUser;

    private LocalDateTime createdDate = LocalDateTime.now();

    private LocalDateTime dueDate = LocalDateTime.now();

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "closed_by_user_id_fk")
    private UserModel closedByUser;

    private LocalDateTime closedDate;

    private boolean isClosed;

    private String remarks;

}
