package com.taskify.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskStats {
    private Long customers;
    private Long tasks;
    private Long newPumpTask;
    private Long serviceTask;
    private Long overdueTasks;
}
