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
            // Kafka 메시지 파싱
            log.info("수신한 Kafka 메시지: {}", message);
            VideoData videoData = objectMapper.readValue(message, VideoData.class);

            // 카테고리별로 비디오 ID 그룹화
            Map<String, List<String>> categoryVideoIds = videoData.getItems().stream()
                    .collect(Collectors.groupingBy(
                            VideoData.VideoItem::getCategoryId, // 카테고리 ID 기준으로 그룹화
                            Collectors.mapping(VideoData.VideoItem::getVideoId, Collectors.toList())
                    ));

            // DB에서 모든 데이터 조회
            List<VideoIdEntity> existingEntities = videoRepository.findAll();
            log.info("DB에서 조회한 기존 데이터: {}", existingEntities);

            // 카테고리별로 기존 데이터를 그룹화
            Map<String, List<VideoIdEntity>> existingEntitiesByCategory = existingEntities.stream()
                    .collect(Collectors.groupingBy(VideoIdEntity::getCategoryId));
            log.info("카테고리별 기존 데이터 그룹화: {}", existingEntitiesByCategory);

            // 카테고리별 데이터 처리
            for (Map.Entry<String, List<String>> entry : categoryVideoIds.entrySet()) {
                String categoryId = entry.getKey();
                List<String> newVideoIds = entry.getValue();
                log.info("처리 중인 카테고리: {}, 새로 들어온 비디오 ID: {}", categoryId, newVideoIds);

                // 현재 카테고리의 기존 비디오 ID
                List<String> existingVideoIds = existingEntitiesByCategory.getOrDefault(categoryId, Collections.emptyList())
                        .stream()
                        .map(VideoIdEntity::getVideoId)
                        .collect(Collectors.toList());
                log.info("[{}] 기존 비디오 ID: {}", categoryId, existingVideoIds);

                // 새로 들어온 데이터 중 기존에 없는 데이터 필터링
                List<String> newEntries = newVideoIds.stream()
                        .filter(id -> !existingVideoIds.contains(id)) // 기존에 없는 데이터만
                        .limit(5 - existingVideoIds.size()) // 최대 5개까지만 추가
                        .collect(Collectors.toList());
                log.info("[{}] 추가할 새 비디오 ID: {}", categoryId, newEntries);

                // 새로운 데이터 추가
                newEntries.forEach(videoId -> {
                    VideoIdEntity videoEntity = new VideoIdEntity();
                    videoEntity.setVideoId(videoId);
                    videoEntity.setCategoryId(categoryId); // 카테고리 ID 설정
                    videoRepository.save(videoEntity);
                    log.info("[{}] 새로 추가된 비디오 ID: {}", categoryId, videoId);
                });

                // 유지할 비디오 ID 목록 생성
                List<String> updatedVideoIds = existingVideoIds.stream()
                        .filter(newVideoIds::contains) // 새로 들어온 데이터 중 기존 데이터 유지
                        .collect(Collectors.toList());
                log.info("[{}] 유지할 비디오 ID: {}", categoryId, updatedVideoIds);

                // 추가된 데이터 포함하여 최종 목록
                updatedVideoIds.addAll(newEntries);
                log.info("[{}] 최종 비디오 ID 목록: {}", categoryId, updatedVideoIds);

                // 기존 데이터 중 유지할 데이터 제외하고 삭제
                videoRepository.deleteNotInVideoIdsByCategory(updatedVideoIds, categoryId);
                log.info("[{}] 삭제 완료: 유지할 비디오 ID 외 모든 데이터 삭제", categoryId);
            }

            log.info("모든 카테고리의 데이터 처리 완료");

        } catch (Exception e) {
            log.error("카프카 메시지 처리 중 에러 발생: ", e);
        }
    }

}
