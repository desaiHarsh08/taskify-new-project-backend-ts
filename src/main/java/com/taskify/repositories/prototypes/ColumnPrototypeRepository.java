package com.taskify.repositories.prototypes;

import com.taskify.models.prototypes.ColumnPrototypeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColumnPrototypeRepository extends JpaRepository<ColumnPrototypeModel, Long> {

    Optional<ColumnPrototypeModel> findByName(String name);

}
