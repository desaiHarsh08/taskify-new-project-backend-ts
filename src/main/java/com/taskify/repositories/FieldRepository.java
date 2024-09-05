package com.taskify.repositories;

import com.taskify.models.FieldModel;
import com.taskify.models.FunctionModel;
import com.taskify.models.UserModel;
import com.taskify.models.prototypes.FieldPrototypeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<FieldModel, Long> {

    Page<FieldModel> findByFieldPrototype(Pageable pageable, FieldPrototypeModel fieldPrototype);

    List<FieldModel> findByFunction(FunctionModel function);

    Page<FieldModel> findByCreatedByUser(Pageable pageable, UserModel createdByUser);

    Page<FieldModel> findByCreatedDate(Pageable pageable, Date createdDate);

    Page<FieldModel> findByClosedByUser(Pageable pageable, UserModel closedByUser);

    Page<FieldModel> findByClosedDate(Pageable pageable, Date closedDate);

    Page<FieldModel> findByIsClosed(Pageable pageable, boolean isClosed);

}
