package com.taskify.services;

import com.taskify.dtos.RoleDto;

import java.util.List;

public interface RoleServices {

    RoleDto createRole(RoleDto role);

    RoleDto getRoleById(Long id);

    List<RoleDto> getRolesByUserId(Long userId);

    boolean deleteRole(Long id);

}
