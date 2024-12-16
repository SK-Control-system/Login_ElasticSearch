package com.example.final_jj.elasticsearch.controller;

import com.example.final_jj.elasticsearch.service.Impl.ElasticSearchService;
import com.example.final_jj.postgreSQL.repository.VideoIdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/video/search/concurrentViewersWithTime")
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

    @GetMapping("/statistics/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyStatisticsByChannel(
            @RequestParam String index,
            @RequestParam String channelId,
            @RequestParam String month) {
        Map<String, Object> statistics = elasticSearchService.getMonthlyStatisticsByChannel(index, channelId, month);
        return ResponseEntity.ok(statistics);
    }

}