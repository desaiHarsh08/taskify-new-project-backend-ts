package com.taskify.models.prototypes;

import com.taskify.constants.ModelConstants;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = ModelConstants.FIELD_COLUMN_PROTOTYPE_TABLE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FieldColumnPrototypeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = FieldPrototypeModel.class)
    private FieldPrototypeModel fieldPrototype;

    @ManyToOne(targetEntity = ColumnPrototypeModel.class)
    private ColumnPrototypeModel columnPrototype;

}
