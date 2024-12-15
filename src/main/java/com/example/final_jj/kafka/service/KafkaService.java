package com.example.final_jj.kafka.service;

import com.example.final_jj.kafka.dto.VideoData;
import com.example.final_jj.kafka.entity.VideoIdEntity;
import com.example.final_jj.kafka.repository.VideoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class KafkaService {

    private final ObjectMapper objectMapper;
    private final VideoRepository videoRepository;

    public KafkaService(ObjectMapper objectMapper, VideoRepository videoRepository) {
        this.objectMapper = objectMapper;
        this.videoRepository = videoRepository;
    }

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics.video-id-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        try {
            // 수신한 Kafka 메시지 로그 출력
            log.info("수신한 Kafka 메시지: {}", message);

            // Kafka에서 수신한 메시지를 VideoData 객체로 변환
            VideoData videoData = objectMapper.readValue(message, VideoData.class);

            // Null category 데이터가 있는 경우 UNKNOWN_CATEGORY로 대체
            videoData.getItems().forEach(item -> {
                if (item.getCategory() == null) {
                    log.warn("Null category detected for videoId: {}, videoTitle: {}",
                            item.getVideoId(), item.getVideoTitle());
                    item.setCategory("UNKNOWN_CATEGORY"); // 기본값 설정
                }
            });

            // 카테고리별 비디오 ID 그룹화 (UNKNOWN_CATEGORY는 제외)
            Map<String, List<String>> categoryVideoIds = videoData.getItems().stream()
                    .filter(item -> !"UNKNOWN_CATEGORY".equals(item.getCategory())) // UNKNOWN_CATEGORY 제외
                    .collect(Collectors.groupingBy(
                            VideoData.VideoItem::getCategory, // 카테고리 기준으로 그룹화
                            Collectors.mapping(VideoData.VideoItem::getVideoId, Collectors.toList())
                    ));

            log.info("그룹화된 데이터: {}", categoryVideoIds);

            // 기존 DB 데이터 조회
            List<VideoIdEntity> existingEntities = videoRepository.findAll();
            log.info("DB에서 조회한 기존 데이터: {}", existingEntities);

            // 기존 데이터 카테고리별로 그룹화
            Map<String, List<VideoIdEntity>> existingEntitiesByCategory = existingEntities.stream()
                    .collect(Collectors.groupingBy(VideoIdEntity::getCategory));

            // 카테고리별 비디오 ID 처리
            for (Map.Entry<String, List<String>> entry : categoryVideoIds.entrySet()) {
                String category = entry.getKey(); // 현재 카테고리
                List<String> newVideoIds = entry.getValue(); // 새로 들어온 비디오 ID 목록

                log.info("처리 중인 카테고리: {}, 새로 들어온 비디오 ID: {}", category, newVideoIds);

                // DB에 저장된 기존 비디오 ID 조회
                List<String> existingVideoIds = existingEntitiesByCategory.getOrDefault(category, Collections.emptyList())
                        .stream()
                        .map(VideoIdEntity::getVideoId)
                        .collect(Collectors.toList());

                // 추가할 새 비디오 ID (최대 5개까지 제한)
                List<String> newEntries = newVideoIds.stream()
                        .filter(id -> !existingVideoIds.contains(id)) // 기존 데이터에 없는 ID만 추가
                        .limit(5 - existingVideoIds.size()) // 최대 5개까지 채우기
                        .collect(Collectors.toList());

                log.info("[{}] 추가할 새 비디오 ID: {}", category, newEntries);

                // 새 비디오 ID를 DB에 저장
                newEntries.forEach(videoId -> {
                    VideoIdEntity videoEntity = new VideoIdEntity();
                    videoEntity.setVideoId(videoId);
                    videoEntity.setCategory(category);
                    videoRepository.save(videoEntity);
                    log.info("[{}] 새로 추가된 비디오 ID: {}", category, videoId);
                });

                // 기존 비디오 ID 중에서 유지할 비디오 ID 목록
                List<String> updatedVideoIds = existingVideoIds.stream()
                        .filter(newVideoIds::contains) // 새로 들어온 비디오 ID 중에서 유지할 항목
                        .collect(Collectors.toList());

                updatedVideoIds.addAll(newEntries); // 새로 추가된 비디오 ID도 포함

                // 기존 비디오 중에서 제외할 항목 삭제
                videoRepository.deleteNotInVideoIdsByCategory(updatedVideoIds, category);
                log.info("[{}] 최종 비디오 ID 목록: {}", category, updatedVideoIds);
            }

            log.info("모든 카테고리의 데이터 처리 완료");

        } catch (Exception e) {
            // 에러 발생 시 로그 출력
            log.error("카프카 메시지 처리 중 에러 발생: ", e);
        }
    }
}
