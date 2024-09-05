package com.taskify.repositories.prototypes;

import com.taskify.models.prototypes.FunctionPrototypeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FunctionPrototypeRepository extends JpaRepository<FunctionPrototypeModel, Long> {

    Optional<FunctionPrototypeModel> findByTitle(String title);

    List<FunctionPrototypeModel> findByDepartment(String department);

}
