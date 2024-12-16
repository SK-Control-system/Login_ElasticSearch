package com.example.final_jj.kafka.repository;

import com.example.final_jj.kafka.entity.MusicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicRepository extends JpaRepository<MusicEntity, Long> {
}
