package com.fitness.activityService.repository;

import com.fitness.activityService.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiviryRepository extends JpaRepository<Activity, String> {
}
