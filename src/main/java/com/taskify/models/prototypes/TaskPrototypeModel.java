package com.taskify.models.prototypes;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.taskify.constants.ModelConstants;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = ModelConstants.TASK_PROTOTYPE_TABLE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskPrototypeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String taskType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            joinColumns = { @JoinColumn(name = "task_prototype_id_fk", referencedColumnName = "id") },
            inverseJoinColumns = { @JoinColumn(name = "function_prototype_id_fk", referencedColumnName = "id") }
    )
    @JsonManagedReference
    private Set<FunctionPrototypeModel> functionPrototypes = new HashSet<>();

    @Override
    public String toString() {
        return "TaskPrototypeModel[id=" + id + ", taskType=" + taskType + "]";
    }

}
