package com.taskify.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStats {

    private Long tasks;
    private Long highPriority;
    private Long pending;
    private Long completed;

}
