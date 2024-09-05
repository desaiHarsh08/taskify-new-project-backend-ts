package com.taskify.models.prototypes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.taskify.constants.ModelConstants;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = ModelConstants.FUNCTION_PROTOTYPE_TABLE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FunctionPrototypeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private boolean isChoice = false;

    @ManyToMany(mappedBy = "functionPrototypes", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<TaskPrototypeModel> taskPrototypes = new HashSet<>();

    @Override
    public String toString() {
        return "FunctionPrototypeModel[id=" + id + "]";
    }

}
