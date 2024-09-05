package com.taskify.services;

import java.util.List;

import com.taskify.dtos.UserDto;
import com.taskify.utils.PageResponse;

public interface UserServices {

    UserDto createUser(UserDto userDto);

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id);

    UserDto getUserByEmail(String email);

    PageResponse<UserDto> getUsersByDepartment(int pageNumber, String department);

    UserDto updateUser(UserDto givenUser);

    boolean deleteUser(Long id);

}
