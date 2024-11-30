package com.example.final_jj.elasticsearch.controller;

import com.example.final_jj.elasticsearch.enums.HttpMethodEnum;
import com.example.final_jj.elasticsearch.factor.ElasticSearchClientFactory;
import com.example.final_jj.elasticsearch.utils.common.ElasticExecutor;
import com.example.final_jj.postgreSQL.entity.VideoEntity;
import com.example.final_jj.postgreSQL.repository.PostgreSQLRepository;
import org.elasticsearch.client.RestClient;
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

    // videoId에 해당되는 emotion.label 데이터 추출
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

    // videoId에 해당되는 sentiment.label 데이터 추출
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

        // sentiment.label추출
        return findValue("chattingAnalysisResult.sentiment.label", filteredResults);
    }

    // videoId로 시청자수 추출
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

    // videoId로 좋아요수 추출
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
        return findValue("videoData.ikeCount", filteredResults); //추후에 videoData.likeCount로 변경하기

    }

    // 워드클라우드 데이터 추출
    @PostMapping("/chatting/wordCloud")
    public List<String> getWordCloud(@RequestParam String index, @RequestBody String queryJson, @RequestParam String videoid) {
        VideoEntity videoEntity = repository.findByVideoid(videoid);
        if (videoEntity == null) {
            throw new RuntimeException("videoid 검색 실패 : " + videoid);
        }
        String videoId = videoEntity.getVideoid();

        List<Map<String, Object>> searchList = searchDocuments(index, queryJson);

        // videoId로 필터링
        List<Map<String, Object>> filteredResults = checkVideoId(videoId, searchList);

        //워드클라우드 데이터 추출
        return findValue("message", filteredResults);
    }
}
