package com.taskify.repositories;

import com.taskify.models.CustomerModel;
import com.taskify.models.ParentCompanyModel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerModel, Long> {

    Optional<CustomerModel> findByEmail(String email);

    Optional<CustomerModel> findByCustomerNameOrEmail(String customerName, String email);

    Optional<CustomerModel> findByCustomerName(String customerName);

    List<CustomerModel> findByParentCompany(ParentCompanyModel parentCompany);

    // New methods for finding customers by email, city, state or their combinations
    List<CustomerModel> findByEmailOrCityOrState(String email, String city, String state);

    List<CustomerModel> findByEmailAndCity(String email, String city);

    List<CustomerModel> findByEmailAndState(String email, String state);

    List<CustomerModel> findByCityAndState(String city, String state);

    List<CustomerModel> findByEmailAndCityAndState(String email, String city, String state);

    @Query("SELECT c FROM CustomerModel c WHERE " +
            "(:email IS NULL OR LOWER(c.email) = LOWER(:email)) AND " +
            "(:city IS NULL OR LOWER(c.city) = LOWER(:city)) AND " +
            "(:state IS NULL OR LOWER( c.state) = LOWER(:state))")
    Page<CustomerModel> findByEmailCityOrState(
            @Param("email") String email,
            @Param("city") String city,
            @Param("state") String state, Pageable pageable);
}
