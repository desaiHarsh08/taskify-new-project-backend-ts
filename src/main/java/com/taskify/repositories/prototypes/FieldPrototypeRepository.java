package com.taskify.repositories.prototypes;

import com.taskify.models.prototypes.FieldPrototypeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FieldPrototypeRepository extends JpaRepository<FieldPrototypeModel, Long> {

    Optional<FieldPrototypeModel> findByTitle(String title);


}
