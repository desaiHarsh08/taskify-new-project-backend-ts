package com.taskify.dtos.prototypes;

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
public class FieldColumnPrototypeDto {

    private Long id;

    private Long fieldPrototypeId;

    private Long columnPrototypeId;

}
