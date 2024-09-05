package com.taskify.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taskify.models.ParentCompanyModel;

@Repository
public interface ParentCompanyRepository extends JpaRepository<ParentCompanyModel, Long> {

    Optional<ParentCompanyModel> findByCompanyName(String companyName);

    Page<ParentCompanyModel> findByState(Pageable pageable, String state);

    Page<ParentCompanyModel> findByCity(Pageable pageable, String city);

    Page<ParentCompanyModel> findByPincode(Pageable pageable, String pincode);

    Page<ParentCompanyModel> findByBusinessType(Pageable pageable, String businessType);

}
