package com.example.final_jj.postgreSQL.repository;

import com.example.final_jj.postgreSQL.entity.SubscribeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscribeRepository extends JpaRepository<SubscribeEntity, Long> {

    @Query("SELECT s.channelId FROM SubscribeEntity s WHERE s.userId = :userId")
    List<String> findChannelIdsByUserId(Long userId);

    @Query("SELECT s.channelId FROM SubscribeEntity s")
    List<String> findAllChannelIds();

}