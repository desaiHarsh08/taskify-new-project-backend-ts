package com.taskify.repositories;

import com.taskify.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByEmail(String email);

//    Page<UserModel> findByIsDisabled(Pageable pageable, boolean isDisabled);

    Page<UserModel> findByDepartment(Pageable pageable, String department);

}
