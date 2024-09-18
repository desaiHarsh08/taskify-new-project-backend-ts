package com.taskify.repositories;

import com.taskify.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;

@Repository
public interface TaskifyTimelineRepository extends JpaRepository<TaskifyTimelineModel, Long> {

    Page<TaskifyTimelineModel> findByUser(Pageable pageable, UserModel user);

    Page<TaskifyTimelineModel> findByResourceType(Pageable pageable, String resourceType);

    Page<TaskifyTimelineModel> findByActionType(Pageable pageable, String actionType);

    Page<TaskifyTimelineModel> findByAtDateBetween(Pageable pageable, LocalDateTime startDate, LocalDateTime endDate);

    Page<TaskifyTimelineModel> findByTaskAbbreviation(Pageable pageable, String taskAbbreviation);

    Page<TaskifyTimelineModel> findByFunctionName(Pageable pageable, String functionName);

    @Query("SELECT t FROM TaskifyTimelineModel t WHERE MONTH(t.atDate) = :month AND YEAR(t.atDate) = :year")
    Page<TaskifyTimelineModel> findByMonthAndYear(Pageable pageable, @Param("month") int month, @Param("year") int year);

}
