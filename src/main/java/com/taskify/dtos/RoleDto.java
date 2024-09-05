package com.taskify.dtos;

import com.taskify.constants.Role;
import lombok.*;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {

    private Long id;

    private Long userId;

    private String roleType = Role.MARKETING.name();

}
