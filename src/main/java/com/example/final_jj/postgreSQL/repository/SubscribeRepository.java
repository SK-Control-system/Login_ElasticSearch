package com.example.final_jj.postgreSQL.repository;

import com.example.final_jj.postgreSQL.entity.SubscribeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<SubscribeEntity, Long> {
    @Query("""
        SELECT r.videoId 
        FROM SubscribeEntity s, ReportEntity r 
        WHERE s.channelId = r.channelId 
        AND s.userId = :userId
        """)
    List<String> findVideoIdsByUserId(@Param("userId") Long userId);

    @Query("""
    SELECT DISTINCT s.channelId
    FROM SubscribeEntity s
    WHERE s.userId = :userId
    """)
    List<String> findChannelIdsByUserId(@Param("userId") Long userId);

}