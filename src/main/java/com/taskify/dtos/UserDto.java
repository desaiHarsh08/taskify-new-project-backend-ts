package com.taskify.dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String name;

    private String email;

    private String password;

    private String phone;

    private boolean isDisabled;

    private String department;

    List<RoleDto> roles = new ArrayList<>();

    private String profileImage;

}
