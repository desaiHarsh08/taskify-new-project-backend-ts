package com.taskify.repositories;

import com.taskify.models.RoleModel;
import com.taskify.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<RoleModel, Long> {

    List<RoleModel> findByUser(UserModel user);

    RoleModel findByUserAndRoleType(UserModel user, String roleType);

}
