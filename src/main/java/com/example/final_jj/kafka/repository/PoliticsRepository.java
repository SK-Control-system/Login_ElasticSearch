package com.example.final_jj.kafka.repository;

import com.example.final_jj.kafka.entity.PoliticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliticsRepository extends JpaRepository<PoliticsEntity, Long> {
}