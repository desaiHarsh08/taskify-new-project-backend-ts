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
@Table(name = ModelConstants.FUNCTION_FIELD_PROTOTYPE_TABLE)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FunctionFieldPrototypeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = FunctionPrototypeModel.class)
    private FunctionPrototypeModel functionPrototype;

    @ManyToOne(targetEntity = FieldPrototypeModel.class)
    private FieldPrototypeModel fieldPrototype;
    
}
