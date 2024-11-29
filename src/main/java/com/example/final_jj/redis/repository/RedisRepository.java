package com.example.final_jj.redis.repository;

import com.example.final_jj.redis.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RedisRepository extends JpaRepository<VideoEntity, String> {
    @Query("SELECT v.videoid FROM VideoEntity v")
    List<String> findAllVideoIds();
}
