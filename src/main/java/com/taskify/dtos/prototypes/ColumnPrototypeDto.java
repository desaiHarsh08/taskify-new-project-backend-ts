package com.taskify.dtos.prototypes;

import com.taskify.constants.ColumnType;
import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ColumnPrototypeDto {

    private Long id;

    private String name;

    private String columnType = ColumnType.STRING.name();

    private boolean isLargeText;

    private boolean isMultipleFiles;

    private boolean notifyCustomer;

}
