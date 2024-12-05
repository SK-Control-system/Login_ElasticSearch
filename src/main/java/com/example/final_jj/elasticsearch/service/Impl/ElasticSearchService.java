package com.example.final_jj.elasticsearch.service.Impl;

import com.example.final_jj.elasticsearch.controller.ElasticSearchController;
import com.example.final_jj.elasticsearch.enums.HttpMethodEnum;
import com.example.final_jj.elasticsearch.factor.ElasticSearchClientFactory;
import com.example.final_jj.elasticsearch.utils.common.ElasticExecutor;
import com.example.final_jj.postgreSQL.entity.ReportEntity;
import com.example.final_jj.postgreSQL.entity.VideoEntity;
import com.example.final_jj.postgreSQL.repository.ReportRepository;
import com.example.final_jj.postgreSQL.repository.SubscribeRepository;
import com.example.final_jj.postgreSQL.repository.VideoIdRepository;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ElasticSearchService {

    @Autowired
    private VideoIdRepository repository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private SubscribeRepository subscribeRepository;


    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchController.class);

    private final RestClient restClient;

    public ElasticSearchService(@Value("${elasticsearch.config.path}") String pathInfo) {
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

    public List<Map<String, Object>> searchDocuments(String index, String queryJson) {
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

    public List<String> searchEmotion(String index, String queryJson, String videoid) {
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


    public List<String> searchSentiment(String index, String queryJson, String videoid) {
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

    public List<String> searchConcurrentViewers(String index, String queryJson, String videoid) {
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

    public List<String> searchConcurrentViewersWithTime(String index, String queryJson, String videoid) {
        VideoEntity videoEntity = repository.findByVideoid(videoid);
        if (videoEntity == null) {
            throw new RuntimeException("videoid 검색 실패 : " + videoid);
        }
        String videoId = videoEntity.getVideoid();

        List<Map<String, Object>> searchList = searchDocuments(index, queryJson);

        // videoId로 필터링
        List<Map<String, Object>> filteredResults = checkVideoId(videoId, searchList);

        // 시청자수 추출
        return findValue("videoData.videoAPIReceivedTime", filteredResults);
    }

    public List<String> searchLikeCount(String index, String queryJson, String videoid) {
        VideoEntity videoEntity = repository.findByVideoid(videoid);
        if (videoEntity == null) {
            throw new RuntimeException("videoid 검색 실패 : " + videoid);
        }
        String videoId = videoEntity.getVideoid();

        List<Map<String, Object>> searchList = searchDocuments(index, queryJson);

        // videoId로 필터링
        List<Map<String, Object>> filteredResults = checkVideoId(videoId, searchList);

        // 좋아요수 추출
        return findValue("videoData.ikeCount", filteredResults); // 추후 videoData.likeCount로 수정해야함
    }

    public List<Map<String, Object>> getWordCloud(String index, String queryJson, String videoid) {

        // searchSourceDocuments용 쿼리 하드코딩
        String searchSourceQuery = "{"
                + "  \"query\": {"
                + "    \"term\": {"
                + "      \"videoId\": \"" + videoid + "\""
                + "    }"
                + "  }"
                + "}";

        // searchSourceDocuments 호출
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

        List<Map<String, Object>> buckets = (List<Map<String, Object>>) topKeywords.get("buckets");
        logger.info("Buckets: {}", buckets);

        return buckets;
    }

    /**
     * Elasticsearch에서 채널 ID로 비디오 데이터를 검색하고 PostgreSQL에 저장
     */
    public List<ReportEntity> processAndSaveReports(Long userId, String index, String queryJson) {
        // 1. 사용자 ID 검증
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        // 2. 구독한 채널 ID 조회
        List<String> channelIds = subscribeRepository.findChannelIdsByUserId(userId);
        if (channelIds == null || channelIds.isEmpty()) {
            throw new IllegalArgumentException("No subscribed channels found for user ID: " + userId);
        }

        // 3. Elasticsearch에서 채널 ID로 비디오 데이터 검색
        List<ReportEntity> allReports = new ArrayList<>();
        for (String channelId : channelIds) {
            List<Map<String, Object>> searchResults = searchDocuments(index, queryJson);

            // 4. Elasticsearch 결과를 ReportEntity로 변환
            List<ReportEntity> reports = searchResults.stream()
                    .filter(result -> channelId.equals(result.get("channelId")))
                    .map(this::mapToReportEntity)
                    .collect(Collectors.toList());

            allReports.addAll(reports);
        }

        // 5. 데이터 저장
        if (!allReports.isEmpty()) {
            logger.info("Saving reports: {}", allReports);
            reportRepository.saveAll(allReports);
        } else {
            logger.warn("No reports to save for user ID: {}", userId);
        }

        return allReports.stream()
                .sorted(Comparator.comparing(ReportEntity::getActualstarttime)) // actualStartTime 기준 정렬
                .collect(Collectors.toList());
    }

    /**
     * Elasticsearch 결과를 ReportEntity로 매핑
     */
    private ReportEntity mapToReportEntity(Map<String, Object> result) {
        Map<String, Object> videoData = (Map<String, Object>) result.get("videoData");
        if (videoData == null) {
            logger.warn("Missing videoData in Elasticsearch result: {}", result);
            return null; // 또는 빈 객체 반환
        }

        ReportEntity report = new ReportEntity();
        report.setVideoId((String) videoData.get("videoId"));
        report.setChannelId((String) videoData.get("channelId"));
        report.setLikecount((String) videoData.getOrDefault("likeCount", "0"));
        report.setConcurrentviewers((String) videoData.getOrDefault("concurrentViewers", "0"));
        report.setVideotitle((String) videoData.get("videoTitle"));
        report.setActualstarttime(videoData.get("actualStartTime") != null
                ? LocalDateTime.parse((String) videoData.get("actualStartTime"))
                : null);

        return report;
    }

    /**
     * 필드별 데이터 추출
     */
    public Map<String, List<?>> extractFieldsFromReports(List<ReportEntity> reports) {
        Map<String, List<?>> fieldData = new HashMap<>();
        fieldData.put("videoIds", reports.stream().map(ReportEntity::getVideoId).collect(Collectors.toList()));
        fieldData.put("likeCounts", reports.stream().map(ReportEntity::getLikecount).collect(Collectors.toList()));
        fieldData.put("concurrentViewers", reports.stream().map(ReportEntity::getConcurrentviewers).collect(Collectors.toList()));
        fieldData.put("titles", reports.stream().map(ReportEntity::getVideotitle).collect(Collectors.toList()));
        fieldData.put("startTimes", reports.stream().map(ReportEntity::getActualstarttime).collect(Collectors.toList()));
        return fieldData;
    }
}
