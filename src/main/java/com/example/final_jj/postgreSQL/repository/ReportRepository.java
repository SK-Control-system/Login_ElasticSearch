package com.example.final_jj.postgreSQL.repository;

import com.example.final_jj.postgreSQL.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    @Query("SELECT r.videoId FROM ReportEntity r WHERE r.channelId IN :channelIds")
    List<String> findVideoIdsByChannelIds(List<String> channelIds);
}