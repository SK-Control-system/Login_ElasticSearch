package com.example.final_jj.elasticsearch.controller;

import com.example.final_jj.elasticsearch.service.Impl.ElasticSearchService;
import com.example.final_jj.postgreSQL.entity.ReportEntity;
import com.example.final_jj.postgreSQL.repository.VideoIdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/es")
public class ElasticSearchController {

    @Autowired
    private VideoIdRepository repository;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @PostMapping("/searchList")
    public ResponseEntity<?> searchDocuments(@RequestParam String index, @RequestBody String queryJson) {
        try {
            List<Map<String, Object>> searchResults = elasticSearchService.searchDocuments(index, queryJson);
            return ResponseEntity.ok(searchResults);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/chatting/search/emotion")
    public ResponseEntity<?> searchEmotion(@RequestParam String index, @RequestBody String queryJson, @RequestParam String videoid) {
        try {
            List<String> emotionLabels = elasticSearchService.searchEmotion(index, queryJson, videoid);
            return ResponseEntity.ok(emotionLabels);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/chatting/search/sentiment")
    public ResponseEntity<?> searchSentiment(@RequestParam String index, @RequestBody String queryJson, @RequestParam String videoid) {
        try {
            List<String> sentimentLabels = elasticSearchService.searchSentiment(index, queryJson, videoid);
            return ResponseEntity.ok(sentimentLabels);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/video/search/concurrentViewers")
    public ResponseEntity<?> searchConcurrentViewers(@RequestParam String index, @RequestBody String queryJson, @RequestParam String videoid) {
        try {
            List<String> concurrentViewers = elasticSearchService.searchConcurrentViewers(index, queryJson, videoid);
            return ResponseEntity.ok(concurrentViewers);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/video/search/searchConcurrentViewersWithTime")
    public ResponseEntity<?> searchConcurrentViewersWithTime(@RequestParam String index, @RequestBody String queryJson, @RequestParam String videoid) {
        try {
            List<String> ConcurrentViewersWithTime = elasticSearchService.searchConcurrentViewersWithTime(index, queryJson, videoid);
            return ResponseEntity.ok(ConcurrentViewersWithTime);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/video/search/likeCount")
    public ResponseEntity<?> searchLikeCount(@RequestParam String index, @RequestBody String queryJson, @RequestParam String videoid) {
        try {
            List<String> likeCounts = elasticSearchService.searchLikeCount(index, queryJson, videoid);
            return ResponseEntity.ok(likeCounts);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/chatting/wordCloud")
    public ResponseEntity<?> getWordCloud( @RequestParam String index, @RequestBody String queryJson, @RequestParam String videoid) {
        try {
            List<Map<String, Object>> wordClouds = elasticSearchService.getWordCloud(index, queryJson, videoid);
            return ResponseEntity.ok(wordClouds);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    /**
     * 사용자 ID를 기반으로 보고서 처리 및 결과 반환
     */
    /**
     * 사용자 ID를 기반으로 Elasticsearch에서 데이터 조회 후 PostgreSQL에 저장
     */
    @PostMapping("/report/save/{userId}")
    public ResponseEntity<List<ReportEntity>> processAndSaveReports(
            @PathVariable Long userId,
            @RequestParam String index,
            @RequestBody String queryJson) {
        try {
            // 서비스 호출
            List<ReportEntity> reports = elasticSearchService.processAndSaveReports(userId, index, queryJson);
            return ResponseEntity.ok(reports); // 정렬된 결과 반환
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    /**
     * 필드별 데이터 추출
     */
    @PostMapping("/report/fields/{userId}")
    public ResponseEntity<Map<String, List<?>>> extractFields(
            @PathVariable Long userId,
            @RequestParam String index,
            @RequestBody String queryJson) {
        try {
            List<ReportEntity> reports = elasticSearchService.processAndSaveReports(userId, index, queryJson);
            Map<String, List<?>> fieldData = elasticSearchService.extractFieldsFromReports(reports);
            return ResponseEntity.ok(fieldData);
        } catch (RuntimeException e) {
            // 예외 발생 시 적절한 타입으로 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyMap());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyMap());
        }
    }
}