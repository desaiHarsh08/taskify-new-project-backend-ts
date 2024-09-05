package com.taskify.repositories.prototypes;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskify.models.prototypes.ColumnPrototypeModel;
import com.taskify.models.prototypes.FieldColumnPrototypeModel;
import com.taskify.models.prototypes.FieldPrototypeModel;

@Repository
public interface FieldColumnProtototypeRepository extends JpaRepository<FieldColumnPrototypeModel, Long> {

    List<FieldColumnPrototypeModel> findByFieldPrototype(FieldPrototypeModel fieldPrototype);

    List<FieldColumnPrototypeModel> findByColumnPrototype(ColumnPrototypeModel columnPrototype);

    Optional<FieldColumnPrototypeModel> findByFieldPrototypeAndColumnPrototype(FieldPrototypeModel fieldPrototype, ColumnPrototypeModel columnPrototype);

}
