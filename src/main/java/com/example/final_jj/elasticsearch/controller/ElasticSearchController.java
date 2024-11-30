package com.example.final_jj.elasticsearch.controller;

import com.example.final_jj.elasticsearch.enums.HttpMethodEnum;
import com.example.final_jj.elasticsearch.factor.ElasticSearchClientFactory;
import com.example.final_jj.elasticsearch.utils.common.ElasticExecutor;
import com.example.final_jj.postgreSQL.entity.VideoEntity;
import com.example.final_jj.postgreSQL.repository.PostgreSQLRepository;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/es")
public class ElasticSearchController {

    @Autowired
    private PostgreSQLRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchController.class);

    private final RestClient restClient;

    public ElasticSearchController(@Value("${elasticsearch.config.path}") String pathInfo) {
        this.restClient = new ElasticSearchClientFactory(pathInfo).getEsClient();
    }

    // checkVideoId를 기준으로 필터링
    public List<Map<String, Object>> checkVideoId(String videoId, List<Map<String, Object>> searchList) {
        List<Map<String, Object>> filteredResults = new ArrayList<>();
        for (Map<String, Object> record : searchList) {
            String recordVideoId = (String) record.get("videoId");
            if (videoId.equals(recordVideoId)) {
                filteredResults.add(record);
            }
        }
        return filteredResults;
    }

    // keyPath를 기반으로 데이터 추출
    public List<String> findValue(String keyPath, List<Map<String, Object>> filteredResults) {
        List<String> values = new ArrayList<>();
        String[] keys = keyPath.split("\\.");
        for (Map<String, Object> record : filteredResults) {
            Object currentValue = record;
            for (String key : keys) {
                if (currentValue instanceof Map) {
                    currentValue = ((Map<?, ?>) currentValue).get(key);
                } else {
                    currentValue = null;
                    break;
                }
            }
            if (currentValue != null) {
                values.add(currentValue.toString());
            }
        }
        return values;
    }

    @PostMapping("/searchList")
    public List<Map<String, Object>> searchDocuments(@RequestParam String index, @RequestBody String queryJson) {
        String path = "/" + index + "/_search";

        List<?> rawResults = ElasticExecutor.searchList(restClient, path, HttpMethodEnum.POST, queryJson, Map.class);
        List<Map<String, Object>> searchResults = new ArrayList<>();

        for (Object result : rawResults) {
            if (result instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> safeResult = (Map<String, Object>) result;
                searchResults.add(safeResult);
            } else {
                throw new RuntimeException("Unexpected data format in search results: " + result.getClass().getName());
            }
        }

        return searchResults;
    }

    // 수정됨: searchSourceDocuments로 검색 수행
    public List<Map<String, Object>> searchSourceDocuments(@RequestParam String index, @RequestBody String queryJson) {
        String path = "/" + index + "/_search";

        try {
            // Elasticsearch 요청 실행
            Map<String, Object> esResponse = ElasticExecutor.searchWordCloud(restClient, path, HttpMethodEnum.POST, queryJson);
            if (esResponse == null || !esResponse.containsKey("hits")) {
                logger.warn("Elasticsearch 응답에 hits 데이터가 없습니다.");
                return new ArrayList<>();
            }

            // hits에서 _source 데이터 추출
            List<Map<String, Object>> results = new ArrayList<>();
            Map<String, Object> hits = (Map<String, Object>) esResponse.get("hits");
            List<Map<String, Object>> hitList = (List<Map<String, Object>>) hits.get("hits");

            for (Map<String, Object> hit : hitList) {
                if (hit.containsKey("_source")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> source = (Map<String, Object>) hit.get("_source");
                    results.add(source);
                }
            }

            return results;
        } catch (Exception e) {
            logger.error("Elasticsearch searchSourceDocuments 요청 실패: ", e);
            return new ArrayList<>();
        }
    }


    @PostMapping("/chatting/search/emotion")
    public List<String> searchEmotion(@RequestParam String index, @RequestBody String queryJson, @RequestParam String videoid) {
        VideoEntity videoEntity = repository.findByVideoid(videoid);
        if (videoEntity == null) {
            throw new RuntimeException("videoid 검색 실패 : " + videoid);
        }
        String videoId = videoEntity.getVideoid();

        List<Map<String, Object>> searchList = searchDocuments(index, queryJson);

        // videoId로 필터링
        List<Map<String, Object>> filteredResults = checkVideoId(videoId, searchList);

        // emotion.label 추출
        return findValue("chattingAnalysisResult.emotion.label", filteredResults);
    }

    @PostMapping("/chatting/search/sentiment")
    public List<String> searchSentiment(@RequestParam String index, @RequestBody String queryJson, @RequestParam String videoid) {
        VideoEntity videoEntity = repository.findByVideoid(videoid);
        if (videoEntity == null) {
            throw new RuntimeException("videoid 검색 실패 : " + videoid);
        }
        String videoId = videoEntity.getVideoid();

        List<Map<String, Object>> searchList = searchDocuments(index, queryJson);

        // videoId로 필터링
        List<Map<String, Object>> filteredResults = checkVideoId(videoId, searchList);

        // sentiment.label 추출
        return findValue("chattingAnalysisResult.sentiment.label", filteredResults);
    }

    @PostMapping("/video/search/concurrentViewers")
    public List<String> searchConcurrentViewers(@RequestParam String index, @RequestBody String queryJson, @RequestParam String videoid) {
        VideoEntity videoEntity = repository.findByVideoid(videoid);
        if (videoEntity == null) {
            throw new RuntimeException("videoid 검색 실패 : " + videoid);
        }
        String videoId = videoEntity.getVideoid();

        List<Map<String, Object>> searchList = searchDocuments(index, queryJson);

        // videoId로 필터링
        List<Map<String, Object>> filteredResults = checkVideoId(videoId, searchList);

        // 시청자수 추출
        return findValue("videoData.concurrentViewers", filteredResults);
    }

    @PostMapping("/video/search/likeCount")
    public List<String> searchLikeCount(@RequestParam String index, @RequestBody String queryJson, @RequestParam String videoid) {
        VideoEntity videoEntity = repository.findByVideoid(videoid);
        if (videoEntity == null) {
            throw new RuntimeException("videoid 검색 실패 : " + videoid);
        }
        String videoId = videoEntity.getVideoid();

        List<Map<String, Object>> searchList = searchDocuments(index, queryJson);

        // videoId로 필터링
        List<Map<String, Object>> filteredResults = checkVideoId(videoId, searchList);

        // 좋아요수 추출
        return findValue("videoData.likeCount", filteredResults); // 수정된 필드 이름 반영
    }

    @PostMapping("/chatting/wordCloud")
    public List<Map<String, Object>> getWordCloud(
            @RequestParam String index,
            @RequestBody String queryJson,
            @RequestParam String videoid) {

//        VideoEntity videoEntity = repository.findByVideoid(videoid);
//        if (videoEntity == null) {
//            throw new RuntimeException("videoid 검색 실패 : " + videoid);
//        }
//        String videoId = videoEntity.getVideoid();

        // searchSourceDocuments용 쿼리 하드코딩
        String searchSourceQuery = "{"
                + "  \"query\": {"
                + "    \"term\": {"
                + "      \"videoId\": \"" + videoid + "\""
                + "    }"
                + "  }"
                + "}";

        // 수정: searchSourceDocuments 호출
        List<Map<String, Object>> searchList = searchSourceDocuments(index, searchSourceQuery);
        logger.info("Search Documents Results: {}", searchList);

        // videoId로 데이터 필터링
        List<Map<String, Object>> filteredResults = checkVideoId(videoid, searchList);
        logger.info("Filtered Results for videoId {}: {}", videoid, filteredResults);

        // 메시지 필드 추출
        List<String> messages = findValue("message", filteredResults);
        logger.info("Extracted Messages: {}", messages);

        // Aggregation 결과 처리
        String path = "/" + index + "/_search";
        Map<String, Object> esResponse = ElasticExecutor.searchWordCloud(restClient, path, HttpMethodEnum.POST, queryJson);

        if (esResponse == null || !esResponse.containsKey("aggregations")) {
            throw new RuntimeException("Elasticsearch 응답에 aggregation 데이터가 없습니다.");
        }

        Map<String, Object> aggregations = (Map<String, Object>) esResponse.get("aggregations");
        Map<String, Object> topKeywords = (Map<String, Object>) aggregations.get("top_keywords");

        if (topKeywords == null || !topKeywords.containsKey("buckets")) {
            throw new RuntimeException("Elasticsearch 응답에 buckets 데이터가 없습니다.");
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> buckets = (List<Map<String, Object>>) topKeywords.get("buckets");
        logger.info("Buckets: {}", buckets);

//        // messages 리스트에 있는 키워드만 buckets에서 필터링
//        List<Map<String, Object>> filteredBuckets = new ArrayList<>();
//        for (Map<String, Object> bucket : buckets) {
//            String key = (String) bucket.get("key");
//            logger.info("Checking bucket key: {}", key);
//            if (messages.contains(key)) {
//                filteredBuckets.add(bucket);
//                logger.info("Added to filteredBuckets: {}", bucket);
//            }
//        }
//
//        logger.info("Filtered Buckets: {}", filteredBuckets);

        return buckets;
    }
}