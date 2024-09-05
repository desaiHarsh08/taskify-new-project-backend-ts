package com.taskify.models.prototypes;

import com.taskify.constants.ColumnType;
import com.taskify.constants.ModelConstants;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = ModelConstants.COLUMN_PROTOTYPE_TABLE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColumnPrototypeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String columnType = ColumnType.STRING.name();

    private boolean isLargeText;

    private boolean isMultipleFiles;

    private boolean notifyCustomer;

}
