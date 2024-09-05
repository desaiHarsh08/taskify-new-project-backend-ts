package com.taskify.services.impl;

import com.taskify.dtos.RoleDto;
import com.taskify.dtos.UserDto;
import com.taskify.exceptions.ResourceNotFoundException;
import com.taskify.models.UserModel;
import com.taskify.repositories.UserRepository;
import com.taskify.services.RoleServices;
import com.taskify.services.UserServices;
import com.taskify.utils.PageResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServicesImpl implements UserServices {

    private static final int PAGE_SIZE = 25;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleServices roleServices;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        // Check for user already exist
        if (this.userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            return null;
        }

        UserModel userModel = this.modelMapper.map(userDto, UserModel.class);

        // Encrypt the raw password
        userModel.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));

        // Create a new user
        UserModel savedUser = this.userRepository.save(userModel);

        userDto.setId(savedUser.getId());

        // Add the user's role
        for (RoleDto roleDto : userDto.getRoles()) {
            System.out.println("roleDto: " + roleDto);
            roleDto.setUserId(savedUser.getId());
            this.roleServices.createRole(roleDto);
        }

        return this.getUserById(userDto.getId());
    }

    @Override
    public List<UserDto> getAllUsers() {
        return this.userModelsToDtos(this.userRepository.findAll());
    }

    @Override
    public UserDto getUserById(Long id) {
        UserModel foundUser = this.userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No user exist for id: " + id));

        return this.userModelToDto(foundUser);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        UserModel foundUser = this.userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("No user exist for email: " + email));

        return this.userModelToDto(foundUser);
    }

    @Override
    public PageResponse<UserDto> getUsersByDepartment(int pageNumber, String department) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should be always greater than 0!");
        }

        Pageable pageable = PageRequest.of(pageNumber, PAGE_SIZE);
        Page<UserModel> pageUser = this.userRepository.findByDepartment(pageable, department);

        List<UserModel> userModels = pageUser.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageUser.getTotalPages(),
                pageUser.getTotalElements(),
                this.userModelsToDtos(userModels));
    }

    @Override
    public UserDto updateUser(UserDto givenUser) {
        UserModel foundUser = this.userRepository.findById(givenUser.getId()).orElseThrow(
                () -> new ResourceNotFoundException("No user exist for id: " + givenUser.getId()));

        // Update the data
        foundUser.setName(givenUser.getName());
        foundUser.setDepartment(givenUser.getDepartment());
        foundUser.setDisabled(givenUser.isDisabled());
        foundUser.setPhone(givenUser.getPhone());
        // Save the changes
        this.userRepository.save(foundUser);

        return null;
    }

    // TODO
    @Override
    public boolean deleteUser(Long id) {
        return false;
    }

    private UserDto userModelToDto(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserDto userDto = this.modelMapper.map(userModel, UserDto.class);
        userDto.setId(userModel.getId());
        userDto.setRoles(this.roleServices.getRolesByUserId(userDto.getId()));

        return userDto;
    }

    private List<UserDto> userModelsToDtos(List<UserModel> userModels) {
        List<UserDto> userDtos = new ArrayList<>();

        if (userModels != null) {
            for (UserModel userModel : userModels) {
                userDtos.add(this.userModelToDto(userModel));
            }
        }

        return userDtos;
    }
}
