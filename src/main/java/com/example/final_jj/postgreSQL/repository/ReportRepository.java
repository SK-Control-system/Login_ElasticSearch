package com.example.final_jj.postgreSQL.repository;

import com.example.final_jj.postgreSQL.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    @Query("SELECT DISTINCT r.videoId FROM ReportEntity r")
    List<String> findDistinctVideoIds();

    @Query("SELECT DISTINCT r.concurrentviewers FROM ReportEntity r")
    List<String> findDistinctConcurrentViewers();

    @Query("SELECT DISTINCT r.likecount FROM ReportEntity r")
    List<String> findDistinctLikeCounts();

    @Query("SELECT DISTINCT r.videotitle FROM ReportEntity r")
    List<String> findDistinctVideoTitles();

    @Query("SELECT DISTINCT r.actualstarttime FROM ReportEntity r")
    List<String> findDistinctActualStartTimes();

    @Query("SELECT DISTINCT r.videothumbnailurl FROM ReportEntity r")
    List<String> findDistinctVideoThumbnailUrls();

    @Query("SELECT DISTINCT r.channelId FROM ReportEntity r")
    List<String> findDistinctChannelIds();

    @Query("SELECT DISTINCT r.channeltitle FROM ReportEntity r")
    List<String> findDistinctChannelTitles();
}
