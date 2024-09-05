package com.taskify.dtos.prototypes;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FunctionPrototypeDto {

    private Long id;

    private String title;

    private String description;

    private String department;

    private boolean isChoice = false;

    List<FieldPrototypeDto> fieldPrototypes = new ArrayList<>();

}
