package com.example.final_jj.kafka.repository;

import com.example.final_jj.kafka.entity.LolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LolRepository extends JpaRepository<LolEntity, Long> {
}
