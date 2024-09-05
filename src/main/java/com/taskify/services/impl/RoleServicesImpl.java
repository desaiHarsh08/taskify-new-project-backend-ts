package com.taskify.services.impl;

import com.taskify.dtos.RoleDto;
import com.taskify.exceptions.ResourceNotFoundException;
import com.taskify.models.RoleModel;
import com.taskify.models.UserModel;
import com.taskify.repositories.RoleRepository;
import com.taskify.repositories.UserRepository;
import com.taskify.services.RoleServices;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServicesImpl implements RoleServices {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public RoleDto createRole(RoleDto role) {
        UserModel foundUser = this.userRepository.findById(role.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("No user exist for id: " + role.getUserId())
        );

        if (this.roleRepository.findByUserAndRoleType(foundUser, role.getRoleType()) != null) {
            return null;
        }
        RoleModel roleModel = this.modelMapper.map(role, RoleModel.class);
        roleModel.setUser(foundUser);

        RoleModel savedRole = this.roleRepository.save(roleModel);
        role.setId(savedRole.getId());

        return role;
    }

    @Override
    public RoleDto getRoleById(Long id) {
        RoleModel foundRole = this.roleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No role exist for id: " + id)
        );

        return this.roleModelToDto(foundRole);
    }

    @Override
    public List<RoleDto> getRolesByUserId(Long userId) {
        UserModel foundUser = this.userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("No user exist for id: " + userId)
        );

        return this.roleModelsToDtos(
                this.roleRepository.findByUser(foundUser)
        );
    }

    @Override
    public boolean deleteRole(Long id) {
        return false;
    }

    private RoleDto roleModelToDto(RoleModel roleModel) {
        if (roleModel == null) {
            return null;
        }
        RoleDto roleDto = this.modelMapper.map(roleModel, RoleDto.class);
        roleDto.setId(roleModel.getId());

        return roleDto;
    }

    private List<RoleDto> roleModelsToDtos(List<RoleModel> roleModels) {
        List<RoleDto> roleDtos = new ArrayList<>();
        if (roleModels != null) {
            for (RoleModel roleModel: roleModels) {
                roleDtos.add(this.roleModelToDto(roleModel));
            }
        }

        return roleDtos;
    }
}
