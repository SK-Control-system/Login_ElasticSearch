package com.example.final_jj.kafka.service;

import com.example.final_jj.kafka.dto.VideoData;
import com.example.final_jj.kafka.entity.VideoIdEntity;
import com.example.final_jj.kafka.repository.VideoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
            VideoData videoData = objectMapper.readValue(message, VideoData.class);

            // 새로운 videoId 추출
            List<String> newVideoIds = videoData.getItems().stream()
                    .map(VideoData.VideoItem::getVideoId)
                    .distinct()
                    .collect(Collectors.toList());

            // 기존 테이블의 videoId 가져오기
            List<String> existingVideoIds = videoRepository.findAll().stream()
                    .map(VideoIdEntity::getVideoId)
                    .collect(Collectors.toList());

            // 유지할 videoId 목록 생성
            List<String> updatedVideoIds = existingVideoIds.stream()
                    .filter(newVideoIds::contains) // 중복된 항목 유지
                    .collect(Collectors.toList());

            // 빈자리 채울 새로운 데이터
            List<String> newEntries = newVideoIds.stream()
                    .filter(id -> !existingVideoIds.contains(id)) // 기존에 없는 데이터
                    .limit(25 - updatedVideoIds.size()) // 25개 제한
                    .collect(Collectors.toList());

            // 최종 videoId 목록
            updatedVideoIds.addAll(newEntries);

            // 테이블 정리: 기존 데이터 중 유지할 목록 제외하고 삭제
            videoRepository.deleteNotInVideoIds(updatedVideoIds);

            // 새로운 데이터 추가
            newEntries.forEach(videoId -> {
                VideoIdEntity videoEntity = new VideoIdEntity();
                videoEntity.setVideoId(videoId);
                videoRepository.save(videoEntity);
            });

            log.info("VideoID 업데이트 : {}", updatedVideoIds);

        } catch (Exception e) {
            log.error("카프카 에러 메시지 : ", e);
        }
    }
}
