package com.taskify.repositories.prototypes;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskify.models.prototypes.FieldPrototypeModel;
import com.taskify.models.prototypes.FunctionFieldPrototypeModel;
import com.taskify.models.prototypes.FunctionPrototypeModel;

@Repository
public interface FunctionFieldPrototypeRepository extends JpaRepository<FunctionFieldPrototypeModel, Long>{

    List<FunctionFieldPrototypeModel> findByFunctionPrototype(FunctionPrototypeModel functionPrototype);

    List<FunctionFieldPrototypeModel> findByFieldPrototype(FieldPrototypeModel fieldPrototype);

    Optional<FunctionFieldPrototypeModel> findByFunctionPrototypeAndFieldPrototype(FunctionPrototypeModel functionPrototype, FieldPrototypeModel fieldPrototype);

}
