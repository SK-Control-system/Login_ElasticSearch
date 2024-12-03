package com.example.final_jj.postgreSQL.repository;

import com.example.final_jj.postgreSQL.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostgreSQLRepository extends JpaRepository<VideoEntity, String> {
    @Query("SELECT v.videoid FROM VideoEntity v")
    List<String> findAllVideoIds();

    VideoEntity findByVideoid(String videoid);
}
