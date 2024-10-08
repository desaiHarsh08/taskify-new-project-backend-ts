package com.taskify.dtos.prototypes;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FunctionFieldPrototypeDto {

    private Long id;

    private Long functionPrototypeId;

    private List<FieldPrototypeDto> fieldPrototypes;

}
