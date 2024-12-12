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

            // 현재 테이블의 데이터 개수 확인
            int existingCount = existingVideoIds.size();
            log.info("현재 데이터 개수: {}개 @@@", existingCount);

            // 조건: 테이블 데이터가 25개 미만이면 삭제 없이 새로운 데이터 추가만 수행
            if (existingCount < 25) {
                // 새로 들어온 데이터 중 기존에 없는 데이터만 필터링
                List<String> newEntries = newVideoIds.stream()
                        .filter(id -> !existingVideoIds.contains(id))
                        .limit(25 - existingCount) // 남은 공간만큼만 추가
                        .collect(Collectors.toList());

                // 새로운 데이터 추가
                newEntries.forEach(videoId -> {
                    VideoIdEntity videoEntity = new VideoIdEntity();
                    videoEntity.setVideoId(videoId);
                    videoRepository.save(videoEntity);
                });

                log.info("초기 데이터 채우기: {}개 추가됨", newEntries.size());
                return; // 여기서 종료, 삭제는 수행하지 않음
            }

            // 유지할 videoId 목록 생성
            List<String> updatedVideoIds = existingVideoIds.stream()
                    .filter(newVideoIds::contains) // 중복된 항목 유지
                    .collect(Collectors.toList());

            // 빈자리 채울 새로운 데이터
            List<String> newEntries = newVideoIds.stream()
                    .filter(id -> !existingVideoIds.contains(id)) // 기존에 없는 데이터
                    .limit(25 - updatedVideoIds.size()) // 25개 제한
                    .collect(Collectors.toList());
            log.info("대체되는 데이터: {}", newEntries);

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
