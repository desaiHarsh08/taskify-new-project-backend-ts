package com.taskify.repositories;

import com.taskify.models.FunctionModel;
import com.taskify.models.TaskModel;
import com.taskify.models.UserModel;
import com.taskify.models.prototypes.FunctionPrototypeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FunctionRepository extends JpaRepository<FunctionModel, Long> {

    Page<FunctionModel> findByFunctionPrototype(Pageable pageable, FunctionPrototypeModel functionPrototype);

    List<FunctionModel> findByTaskOrderByIdDesc(TaskModel task);

    Page<FunctionModel> findByDepartment(Pageable pageable, String department);

    Page<FunctionModel> findByCreatedByUser(Pageable pageable, UserModel createdByUser);

    Page<FunctionModel> findByCreatedDate(Pageable pageable, Date createdDate);

    Page<FunctionModel> findByDueDate(Pageable pageable, Date dueDate);

    Page<FunctionModel> findByClosedByUser(Pageable pageable, UserModel closedByUser);

    Page<FunctionModel> findByClosedDate(Pageable pageable, Date closedDate);

    Page<FunctionModel> findByIsClosed(Pageable pageable, Boolean isClosed);


}
