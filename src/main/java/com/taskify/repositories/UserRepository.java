package com.taskify.repositories;

import com.taskify.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByEmail(String email);

//    Page<UserModel> findByIsDisabled(Pageable pageable, boolean isDisabled);

    List<UserModel> findByDepartment(String department);

    @Query("SELECT u FROM UserModel u JOIN RoleModel r ON u.id = r.user.id WHERE u.department = :department AND r.roleType = :roleType")
    List<UserModel> findByDepartmentAndRole(String department, String roleType);

}
