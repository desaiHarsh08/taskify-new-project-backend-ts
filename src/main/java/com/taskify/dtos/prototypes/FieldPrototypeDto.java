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
public class FieldPrototypeDto {

    private Long id;

    private String title;

    private String description;

    List<ColumnPrototypeDto> columnPrototypes = new ArrayList<>();

}
