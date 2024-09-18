package com.taskify.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taskify.models.ParentCompanyModel;

@Repository
public interface ParentCompanyRepository extends JpaRepository<ParentCompanyModel, Long> {

    Optional<ParentCompanyModel> findByCompanyName(String companyName);

    Page<ParentCompanyModel> findByState(Pageable pageable, String state);

    Page<ParentCompanyModel> findByCity(Pageable pageable, String city);

    Page<ParentCompanyModel> findByPincode(Pageable pageable, String pincode);

    Page<ParentCompanyModel> findByBusinessType(Pageable pageable, String businessType);

    @Query("SELECT p FROM ParentCompanyModel p WHERE " +
            "(:companyName IS NULL OR :companyName = '' OR LOWER(p.companyName) LIKE LOWER(CONCAT('%', :companyName, '%'))) AND "
            +
            "(:city IS NULL OR :city = '' OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
            "(:state IS NULL OR :state = '' OR LOWER(p.state) LIKE LOWER(CONCAT('%', :state, '%'))) AND " +
            "(:pincode IS NULL OR :pincode = '' OR p.pincode = :pincode)")
    Page<ParentCompanyModel> findByCompanyNameCityStatePincode(
            @Param("companyName") String companyName,
            @Param("city") String city,
            @Param("state") String state,
            @Param("pincode") String pincode, Pageable pageable);

}
