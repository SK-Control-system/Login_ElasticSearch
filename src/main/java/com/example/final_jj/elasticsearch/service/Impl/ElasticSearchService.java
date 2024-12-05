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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ElasticSearchService {

    @Autowired
    private VideoIdRepository videoIdRepository;

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private ReportRepository reportRepository;
    
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
        VideoEntity videoEntity = videoIdRepository.findByVideoid(videoid);
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
        VideoEntity videoEntity = videoIdRepository.findByVideoid(videoid);
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
        VideoEntity videoEntity = videoIdRepository.findByVideoid(videoid);
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
        VideoEntity videoEntity = videoIdRepository.findByVideoid(videoid);
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
        VideoEntity videoEntity = videoIdRepository.findByVideoid(videoid);
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

    /*
        사용자 아이디로 구독한 채널아이디 검색
        -> 채널아이디로 저장된 비디오아이디 리스트 검색
        -> 선택된 비디오아이디로 모든 데이터 검색
        -> actualStartTime 를 기준으로 오름차순 정렬
        -> 필드별 데이터 리스트로 뽑기
        -> DB에 저장
        -> 위 함수들처럼 각각의 필트 데이터들 뿌려주기
     */
    public List<String> getChannelIdByUserId(long userId) {
        List<String> channelIds = subscribeRepository.findChannelIdsByUserId(userId);
        return channelIds;
    }

//    public List<String> getVideoIdsByChannelId(String index, String queryJson, String channelid) {
//        List<String> videoIdList = new ArrayList<>();
//        //String path = "/" + index + "/_search";
//
//        // 1. video템플릿에서 해당 채널 아이디가 들어간 비디오 아이디 모두 검색 -> 2. report 테이블에 저장 ->  3. 비디오 아이디만 반환
//        // searchSourceDocuments용 쿼리 하드코딩
//        String searchSourceQuery = "{"
//                + "  \"query\": {"
//                + "    \"term\": {"
//                + "      \"channelId\": \"" + channelid + "\""
//                + "    }"
//                + "  }"
//                + "}";
//        List<Map<String, Object>> searchList = searchSourceDocuments(index, searchSourceQuery);//채널아이디가 들어간 데이터들
//        List<String > videoIds = findValue("videoId", searchList); //특정 채널아이디가 포함된 데이터에서 videoId만 필터링
//
//        for(String videoId : videoIds) {
//            String id = reportRepository.save(videoId);
//            videoIdList.add(id);
//        }
//
//        return videoIdList;
//
//    }
//
//    public void saveReportDataByVideoId(/*String videoId,*/ String index, String queryJson) {
//        // 선택된 비디오아이디로 모든 데이터 검색 -> actualStartTime 를 기준으로 오름차순 정렬 -> 필드별 데이터 리스트로 뽑기 -> DB에 저장
//
//        List<Map<String, Object>> filteredResults = searchDocuments(index, queryJson); //쿼리로 비디오 아이디에 해당되는 데이터 오름차순으로 정렬
//
//        List<String> videoId = findValue("videoData.videoId", filteredResults);
//        reportRepository.save(videoId);
//        List<String> concurrentViewers = findValue("videoData.concurrentViewers", filteredResults);
//        reportRepository.save(concurrentViewers);
//        List<String> likeCount = findValue("videoData.likeCount", filteredResults);
//        reportRepository.save(likeCount);
//        List<String> videoTitle = findValue("videoData.videoTitle", filteredResults);
//        reportRepository.save(videoTitle);
//        List<String> actualStartTime = findValue("videoData.actualStartTime", filteredResults);
//        reportRepository.save(actualStartTime);
//        List<String> channelId = findValue("videoData.channelId", filteredResults);
//        reportRepository.save(channelId);
//        List<String> channelTitle = findValue("videoData.channelTitle", filteredResults);
//        reportRepository.save(channelTitle);
//
//    }

    public List<String> getVideoIdsByChannelId(String index, String queryJson, String channelid) {
        List<String> videoIdList = new ArrayList<>();

        String searchSourceQuery = "{"
                + "  \"query\": {"
                + "    \"term\": {"
                + "      \"videoData.channelId\": {"
                + "        \"value\": \"" + channelid + "\""
                + "      }"
                + "    }"
                + "  }"
                + "}";


        // 채널 아이디로 비디오 데이터 검색
        List<Map<String, Object>> searchList = searchSourceDocuments(index, searchSourceQuery);
        List<String> videoIds = findValue("videoId", searchList);

        // 비디오 아이디 리스트를 저장하고 반환
        List<ReportEntity> reports = videoIds.stream()
                .map(videoId -> {
                    ReportEntity report = new ReportEntity();
                    report.setVideoId(videoId);
                    // 필요한 다른 필드 설정
                    return report;
                })
                .collect(Collectors.toList());

        reportRepository.saveAll(reports);


        return videoIdList;
    }

    public Map<String, List<String>> saveReportDataByVideoId(String index, String queryJson) {
        // 쿼리를 실행하여 데이터 검색, 오름차순까지하기
        List<Map<String, Object>> filteredResults = searchDocuments(index, queryJson);

//        // 데이터를 정렬
//        filteredResults.sort((a, b) -> {
//            String timeA = (String) a.getOrDefault("videoData.actualStartTime", "");
//            String timeB = (String) b.getOrDefault("videoData.actualStartTime", "");
//            return timeA.compareTo(timeB);
//        });

        // 각 필드별 데이터 추출
        List<String> videoId = findValue("videoData.videoId", filteredResults);
        reportRepository.save(videoId);

        List<String> concurrentViewers = findValue("videoData.concurrentViewers", filteredResults);
        reportRepository.save(concurrentViewers);

        List<String> likeCount = findValue("videoData.likeCount", filteredResults);
        reportRepository.save(likeCount);

        List<String> videoTitle = findValue("videoData.videoTitle", filteredResults);
        reportRepository.save(videoTitle);

        List<String> actualStartTime = findValue("videoData.actualStartTime", filteredResults);
        reportRepository.save(actualStartTime);

        List<String> channelId = findValue("videoData.channelId", filteredResults);
        reportRepository.save(channelId);

        List<String> channelTitle = findValue("videoData.channelTitle", filteredResults);
        reportRepository.save(channelTitle);

        // 필터 데이터들을 Map에 담아 반환
        Map<String, List<String>> filteredData = new HashMap<>();
        filteredData.put("videoId", videoId);
        filteredData.put("concurrentViewers", concurrentViewers);
        filteredData.put("likeCount", likeCount);
        filteredData.put("videoTitle", videoTitle);
        filteredData.put("actualStartTime", actualStartTime);
        filteredData.put("channelId", channelId);
        filteredData.put("channelTitle", channelTitle);

        return filteredData;
    }


}
