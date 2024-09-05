package com.taskify.models;

import com.taskify.constants.ModelConstants;
import com.taskify.models.prototypes.FieldPrototypeModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = ModelConstants.FIELD_TABLE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FieldModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = FieldPrototypeModel.class)
    @JoinColumn(name = "field_prototype_id_fk")
    private FieldPrototypeModel fieldPrototype;

    @ManyToOne(targetEntity = FunctionModel.class)
    @JoinColumn(name = "function_id_fk")
    private FunctionModel function;

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "created_by_user")
    private UserModel createdByUser;

    private LocalDateTime createdDate = LocalDateTime.now();

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "closed_by_user")
    private UserModel closedByUser;

    private LocalDateTime closedDate;

    private boolean isClosed;

    private LocalDateTime lastEdited = LocalDateTime.now();

}
