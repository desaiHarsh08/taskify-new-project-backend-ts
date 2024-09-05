package com.taskify.repositories.prototypes;

import com.taskify.models.prototypes.TaskPrototypeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskPrototypeRepository extends JpaRepository<TaskPrototypeModel, Long> {

    Optional<TaskPrototypeModel> findByTaskType(String taskType);

}
