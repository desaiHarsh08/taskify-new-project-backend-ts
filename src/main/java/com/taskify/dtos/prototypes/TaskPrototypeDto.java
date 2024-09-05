package com.taskify.dtos.prototypes;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskPrototypeDto {

    private Long id;

    private String taskType;

    private Set<FunctionPrototypeDto> functionPrototypes = new HashSet<>();

}
