package com.damienwesterman.defensedrill.rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.damienwesterman.defensedrill.rest_api.entity.DrillEntity;

@Repository
public interface DrillRepo extends JpaRepository<DrillEntity, Long> {
    DrillEntity findByNameIgnoreCase(String name);
}
