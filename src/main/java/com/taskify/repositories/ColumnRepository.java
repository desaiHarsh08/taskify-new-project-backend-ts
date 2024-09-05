package com.taskify.repositories;

import com.taskify.models.ColumnModel;
import com.taskify.models.FieldModel;
import com.taskify.models.prototypes.ColumnPrototypeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnRepository extends JpaRepository<ColumnModel, Long> {

    Page<ColumnModel> findByColumnPrototype(Pageable pageable, ColumnPrototypeModel columnPrototype);

    List<ColumnModel> findByField(FieldModel field);

}
