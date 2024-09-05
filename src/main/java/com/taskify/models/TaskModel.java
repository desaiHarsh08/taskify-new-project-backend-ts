package com.taskify.models;

import com.taskify.constants.ModelConstants;
import com.taskify.constants.PriorityType;
import com.taskify.models.prototypes.TaskPrototypeModel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = ModelConstants.TASK_TABLE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = CustomerModel.class)
    @JoinColumn(name = "customer_id_fk")
    private CustomerModel customer;

    @ManyToOne(targetEntity = TaskPrototypeModel.class)
    @JoinColumn(name = "task_prototype_id_fk")
    private TaskPrototypeModel taskPrototype;

    private String priorityType = PriorityType.NORMAL.name();

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "created_by_user_id_fk")
    private UserModel createdByUser;

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "assigned_to_user_id_fk")
    private UserModel assignedToUser;

    private LocalDateTime createdDate = LocalDateTime.now();

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "closed_by_user_id_fk")
    private UserModel closedByUser;

    private  LocalDateTime closedDate;

    private boolean isClosed;

    private String taskAbbreviation;

    private String pumpType;

    private String pumpManufacturer;

    private String specifications;

    private String requirements;

    private String problemDescription;

}
