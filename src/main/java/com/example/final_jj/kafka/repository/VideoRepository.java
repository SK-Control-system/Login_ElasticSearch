package com.example.final_jj.kafka.repository;

import com.example.final_jj.kafka.entity.VideoIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends JpaRepository<VideoIdEntity, Long> {
    // videoId로 데이터 검색
    boolean existsByVideoId(String videoId);

    // 중복되지 않는 데이터를 삭제
    @Modifying
    @Query(value = "DELETE FROM VideoIdEntity v WHERE v.videoId NOT IN :videoIds")
    void deleteNotInVideoIds(List<String> videoIds);

    @Modifying
    @Query(value = "DELETE FROM VideoIdEntity v WHERE v.videoId NOT IN :videoIds AND v.category = :category")
    void deleteNotInVideoIdsByCategory(@Param("videoIds") List<String> videoIds, @Param("category") String category);



}


