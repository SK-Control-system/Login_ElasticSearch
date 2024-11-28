package ES_test.laon.ES_test.controller;

import ES_test.laon.ES_test.ElasticExecutor;
import ES_test.laon.ES_test.config.ElasticSearchClientFactory;
import ES_test.laon.ES_test.HttpMethodEnum;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestClient;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/es")
public class ElasticSearchController {

    private final RestClient restClient;

    public ElasticSearchController() {
        this.restClient = new ElasticSearchClientFactory("jj").getEsClient();
    }

    public List<String> findValue(String keyPath, List<Object> searchList) {
        List<String> values = new ArrayList<>();
        try {
            // ObjectMapper를 사용하여 List를 JSON 문자열로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String searchList2Json = objectMapper.writeValueAsString(searchList); // List를 JSON 문자열로 변환

            // JSON 문자열을 JsonNode로 변환
            JsonNode rootNode = objectMapper.readTree(searchList2Json); // JSON 문자열 파싱

            // 점(.)으로 구분된 경로를 처리
            String[] keys = keyPath.split("\\."); // 키를 "." 기준으로 분리

            // JSON 배열을 순회하며 지정된 key 값 추출
            for (JsonNode node : rootNode) {
                JsonNode currentNode = node;
                for (String key : keys) {
                    currentNode = currentNode.path(key); // 계층적으로 탐색
                    if (currentNode.isMissingNode()) {
                        break; // 중간에 없는 키가 발견되면 종료
                    }
                }
                if (!currentNode.isMissingNode()) {
                    values.add(currentNode.asText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while extracting key: " + keyPath + " - " + e.getMessage());
        }
        return values;
    }

    /**
     * Elasticsearch에서 쿼리 기반으로 데이터를 검색합니다.
     *
     * @param index     검색할 인덱스 이름
     * @param queryJson 검색 조건(JSON 형식)
     * @return 검색된 데이터 리스트
     */
    @PostMapping("/searchList")
    public List<Object> searchDocuments(@RequestParam String index, @RequestBody String queryJson) {
        String path = "/" + index + "/_search";
        return ElasticExecutor.searchList(restClient, path, HttpMethodEnum.POST, queryJson, Object.class);
    }

    @PostMapping("/chatting/search/emotion")
    public List<String> searchEmotion(@RequestParam String index, @RequestBody String queryJson) {
        String path = "/" + index + "/_search";
        List searchList = ElasticExecutor.searchList(restClient, path, HttpMethodEnum.POST, queryJson, Object.class);

//        List<String> emotionLabels = new ArrayList<String>();
//        try {
//            // ObjectMapper를 사용하여 각 Object에서 emotion.label 추출
//            ObjectMapper objectMapper = new ObjectMapper();
//            for (Object obj : searchList) {
//                // Object를 JsonNode로 변환
//                JsonNode rootNode = objectMapper.valueToTree(obj);
//
//                // emotion.label 값 추출
//                JsonNode emotionLabelNode = rootNode.path("chattingAnalysisResult").path("emotion").path("label");
//                if (!emotionLabelNode.isMissingNode()) {
//                    emotionLabels.add(emotionLabelNode.asText());
//                }
//            }
        return findValue("chattingAnalysisResult.emotion.label", searchList);
    }

    @PostMapping("/chatting/search/sentiment")
    public List<String> searchSentiment(@RequestParam String index, @RequestBody String queryJson) {
        String path = "/" + index + "/_search";
        List searchList = ElasticExecutor.searchList(restClient, path, HttpMethodEnum.POST, queryJson, Object.class);

        List<String> sentimentLabels = new ArrayList<String>();
//        try {
//            // ObjectMapper를 사용하여 각 Object에서 sentiment.label 추출
//            ObjectMapper objectMapper = new ObjectMapper();
//            for (Object obj : searchList) {
//                // Object를 JsonNode로 변환
//                JsonNode rootNode = objectMapper.valueToTree(obj);
//
//                // sentiment.label 값 추출
//                JsonNode sentimentLabelNode = rootNode.path("chattingAnalysisResult").path("sentiment").path("label");
//                if (!sentimentLabelNode.isMissingNode()) {
//                    sentimentLabels.add(sentimentLabelNode.asText());
//                }
//            }
//        }
        return findValue("chattingAnalysisResult.sentiment.label", searchList);
    }

    @PostMapping("/vedio/search/concurrentViewers")
    public List<String> searchConcurrentViewers(@RequestParam String index, @RequestBody String queryJson) {
        String path = "/" + index + "/_search";
        List searchList = ElasticExecutor.searchList(restClient, path, HttpMethodEnum.POST, queryJson, Object.class);

        return findValue("videoData.concurrentViewers", searchList);
    }

}


