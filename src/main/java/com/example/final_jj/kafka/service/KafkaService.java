package com.example.final_jj.kafka.service;

import com.example.final_jj.kafka.dto.VideoData;
import com.example.final_jj.kafka.entity.*;
import com.example.final_jj.kafka.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KafkaService {

    private final ObjectMapper objectMapper;
    private final VideoRepository videoRepository;

    @Autowired
    private LolRepository lolRepository;
    @Autowired
    private MapleRepository mapleRepository;
    @Autowired
    private BattlegroundRepository battlegroundRepository;
    @Autowired
    private PoliticsRepository politicsRepository;
    @Autowired
    private MusicRepository musicRepository;

    public KafkaService(ObjectMapper objectMapper, VideoRepository videoRepository) {
        this.objectMapper = objectMapper;
        this.videoRepository = videoRepository;
    }

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics.video-id-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        try {
            // Kafka 메시지 파싱
            VideoData videoData = objectMapper.readValue(message, VideoData.class);

            // 카테고리와 새로운 videoId 추출
            String category = videoData.getCategory();
            List<String> newVideoIds = videoData.getItems().stream()
                    .map(VideoData.VideoItem::getVideoId)
                    .distinct()
                    .limit(25) // 최대 25개 제한
                    .collect(Collectors.toList());

            log.info("카테고리: {}, 새로운 데이터: {}", category, newVideoIds);

            // 카테고리별로 처리
            switch (category) {
                case "정치":
                    resetTableAndSaveData(politicsRepository, newVideoIds, PoliticsEntity.class);
                    break;
                case "리그오브레전드":
                    resetTableAndSaveData(lolRepository, newVideoIds, LolEntity.class);
                    break;
                case "메이플스토리":
                    resetTableAndSaveData(mapleRepository, newVideoIds, MapleEntity.class);
                    break;
                case "배틀그라운드":
                    resetTableAndSaveData(battlegroundRepository, newVideoIds, BattlegroundEntity.class);
                    break;
                case "음악":
                    resetTableAndSaveData(musicRepository, newVideoIds, MusicEntity.class);
                    break;
                default:
                    log.warn("알 수 없는 카테고리: {}", category);
            }


        } catch (Exception e) {
            log.error("카프카 메시지 처리 중 에러 발생: ", e);
        }
    }

    private <T> void resetTableAndSaveData(JpaRepository<T, Long> repository, List<String> newVideoIds, Class<T> entityType) {
        repository.deleteAll(); // 기존 데이터 삭제
        newVideoIds.forEach(videoId -> {
            try {
                T entity = entityType.getDeclaredConstructor().newInstance();
                entity.getClass().getMethod("setVideoId", String.class).invoke(entity, videoId);
                repository.save(entity);
            } catch (Exception e) {
                throw new RuntimeException("데이터 삽입 실패", e);
            }
        });
        log.info("테이블 초기화 및 데이터 삽입 완료");

        // 통합 테이블 업데이트
        updateAllVideosTable();
    }

    private void updateAllVideosTable() {
        // 모든 카테고리 테이블에서 videoId를 가져오기
        List<String> allVideoIds = new ArrayList<>();
        allVideoIds.addAll(politicsRepository.findAll().stream().map(PoliticsEntity::getVideoId).collect(Collectors.toList()));
        allVideoIds.addAll(lolRepository.findAll().stream().map(LolEntity::getVideoId).collect(Collectors.toList()));
        allVideoIds.addAll(mapleRepository.findAll().stream().map(MapleEntity::getVideoId).collect(Collectors.toList()));
        allVideoIds.addAll(battlegroundRepository.findAll().stream().map(BattlegroundEntity::getVideoId).collect(Collectors.toList()));
        allVideoIds.addAll(musicRepository.findAll().stream().map(MusicEntity::getVideoId).collect(Collectors.toList()));

        // 통합 테이블 초기화
        videoRepository.deleteAllData();

        // 통합 테이블에 새로운 videoId 저장
        allVideoIds.stream().distinct().forEach(videoId -> {
            VideoIdEntity entity = new VideoIdEntity();
            entity.setVideoId(videoId);
            videoRepository.save(entity);
        });

        log.info("통합 테이블 업데이트 완료: {}개", allVideoIds.size());
    }
}
