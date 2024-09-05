package com.taskify.repositories;

import com.taskify.models.CustomerModel;
import com.taskify.models.TaskModel;
import com.taskify.models.UserModel;
import com.taskify.models.prototypes.TaskPrototypeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel, Long> {

    Page<TaskModel> findByTaskPrototype(Pageable pageable, TaskPrototypeModel taskPrototype);

    Page<TaskModel> findByPriorityType(Pageable pageable, String priorityType);

    Page<TaskModel> findByCreatedByUser(Pageable pageable, UserModel createdByUser);

    Page<TaskModel> findByAssignedToUser(Pageable pageable, UserModel assignedToUser);

    Page<TaskModel> findByCreatedDate(Pageable pageable, Date createdDate);

    Page<TaskModel> findByClosedDate(Pageable pageable, Date closedDate);

    Page<TaskModel> findByIsClosed(Pageable pageable, Boolean isClosed);

    Page<TaskModel> findByClosedByUser(Pageable pageable, UserModel closedByUser);

    List<TaskModel> findByTaskPrototype(TaskPrototypeModel taskPrototype);

    Optional<TaskModel> findByTaskAbbreviation(String taskAbbreviation);

    List<TaskModel> findByCustomer(CustomerModel customer);

    @Query("SELECT t FROM TaskModel t WHERE EXTRACT(YEAR FROM t.createdDate) = :year AND EXTRACT(MONTH FROM t.createdDate) = :month ORDER BY t.createdDate DESC")
    List<TaskModel> findTasksByYearAndMonth(@Param("year") int year, @Param("month") int month);

}
