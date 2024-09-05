package com.taskify.models;

import com.taskify.constants.ModelConstants;
import com.taskify.models.prototypes.ColumnPrototypeModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = ModelConstants.COLUMN_TABLE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColumnModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = ColumnPrototypeModel.class)
    @JoinColumn(name = "column_prototype_id_fk")
    private ColumnPrototypeModel columnPrototype;

    @ManyToOne(targetEntity = UserModel.class)
    private UserModel createdByUser;

    @ManyToOne(targetEntity = FieldModel.class)
    @JoinColumn(name = "field_id_fk")
    private FieldModel field;

    private Long numberValue;

    private String textValue;

    private boolean booleanValue;

    private String fileDirectoryPath;

}
