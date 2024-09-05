package com.taskify.utils;

import com.taskify.constants.ActionType;
import com.taskify.constants.ResourceType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class NotificationMessage<T> {

    private String action = ActionType.CREATE.name();

    private String resourceType = ResourceType.TASK.name();

    private T data;

}
