package com.example.final_jj.postgreSQL.repository;

import com.example.final_jj.postgreSQL.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    // 채널 ID 리스트를 기반으로 비디오 ID를 검색
    @Query("SELECT r.videoId FROM ReportEntity r WHERE r.channelId IN :channelIds")
    List<String> findVideoIdsByChannelIds(@Param("channelIds") List<String> channelIds);


    // 특정 비디오 ID로 보고서 데이터 가져오기
    @Query("SELECT r FROM ReportEntity r WHERE r.videoId = :videoId")
    ReportEntity findByVideoId(@Param("videoId") String videoId);
}