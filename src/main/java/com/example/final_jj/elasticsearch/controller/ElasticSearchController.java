package com.example.final_jj.elasticsearch.controller;

import com.example.final_jj.elasticsearch.service.Impl.ElasticSearchService;
import com.example.final_jj.postgreSQL.repository.ReportRepository;
import com.example.final_jj.postgreSQL.repository.VideoIdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/es")
public class ElasticSearchController {

    @Autowired
    private VideoIdRepository repository;

    @Autowired
    private ReportRepository reportRepository;

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
    public ResponseEntity<?> searchEmotion(@RequestParam String index, @RequestParam String videoid) {
        try {
            List<String> emotionLabels = elasticSearchService.searchEmotion(index, videoid);
            return ResponseEntity.ok(emotionLabels);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/chatting/search/sentiment")
    public ResponseEntity<?> searchSentiment(@RequestParam String index, @RequestParam String videoid) {
        try {
            List<String> sentimentLabels = elasticSearchService.searchSentiment(index, videoid);
            return ResponseEntity.ok(sentimentLabels);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/video/search/concurrentViewers")
    public ResponseEntity<?> searchConcurrentViewers(@RequestParam String index, @RequestParam String videoid) {
        try {
            List<String> concurrentViewers = elasticSearchService.searchConcurrentViewers(index, videoid);
            return ResponseEntity.ok(concurrentViewers);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/video/search/searchConcurrentViewersWithTime")
    public ResponseEntity<?> searchConcurrentViewersWithTime(@RequestParam String index, @RequestParam String videoid) {
        try {
            List<String> ConcurrentViewersWithTime = elasticSearchService.searchConcurrentViewersWithTime(index, videoid);
            return ResponseEntity.ok(ConcurrentViewersWithTime);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PostMapping("/video/search/likeCount")
    public ResponseEntity<?> searchLikeCount(@RequestParam String index, @RequestParam String videoid) {
        try {
            List<String> likeCounts = elasticSearchService.searchLikeCount(index, videoid);
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

    @PostMapping("/channel-ids")
    public ResponseEntity<List<String>> getChannelIdByUserId(@RequestParam long userId) {
        List<String> channelIds = elasticSearchService.getChannelIdByUserId(userId);
        return ResponseEntity.ok(channelIds);
    }

    @PostMapping("/video-ids")
    public ResponseEntity<List<String>> getVideoIdsByChannelId(@RequestParam String index, @RequestParam String channelId) {
        List<String> videoIds = elasticSearchService.getVideoIdsByChannelId(index, channelId);
        return ResponseEntity.ok(videoIds);
    }

    @PostMapping("/report-data")
    public ResponseEntity<Map<String, List<String>>> saveReportDataByVideoId(@RequestParam String index, @RequestBody String queryJson) {
        Map<String, List<String>> reportData = elasticSearchService.saveReportDataByVideoId(index, queryJson);
        return ResponseEntity.ok(reportData);
    }

    @GetMapping("/fetch")
    public ResponseEntity<Map<String, List<String>>> fetchReportData() {
        // 각 필드별 데이터 조회
        List<String> videoIds = reportRepository.findDistinctVideoIds();
        List<String> concurrentViewers = reportRepository.findDistinctConcurrentViewers();
        List<String> likeCounts = reportRepository.findDistinctLikeCounts();
        List<String> videoTitles = reportRepository.findDistinctVideoTitles();
        List<String> actualStartTimes = reportRepository.findDistinctActualStartTimes();
        List<String> videoThumbnailUrls = reportRepository.findDistinctVideoThumbnailUrls();
        List<String> channelIds = reportRepository.findDistinctChannelIds();
        List<String> channelTitles = reportRepository.findDistinctChannelTitles();

        // 데이터를 Map에 담아 프론트로 반환
        Map<String, List<String>> response = new HashMap<>();
        response.put("videoIds", videoIds);
        response.put("concurrentViewers", concurrentViewers);
        response.put("likeCounts", likeCounts);
        response.put("videoTitles", videoTitles);
        response.put("actualStartTimes", actualStartTimes);
        response.put("videoThumbnailUrls", videoThumbnailUrls);
        response.put("channelIds", channelIds);
        response.put("channelTitles", channelTitles);

        return ResponseEntity.ok(response);
    }

}