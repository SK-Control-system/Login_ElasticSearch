package com.example.final_jj.elasticsearch.service.Impl;//package com.example.elasticsearch.service.Impl;
//
//import com.example.elasticsearch.enums.HttpMethodEnum;
//import com.example.elasticsearch.utils.common.ElasticExecutor;
//import org.elasticsearch.client.RestClient;
//
//import java.util.List;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.util.ArrayList;
//
//public class ElasticExecutorImpl implements ElasticExecutor {
//
//    private static <E> List<E> parseResults(String responseBody, Class<E> clazz) {
//        List<E> results = new ArrayList<>();
//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(responseBody);
//
//            // "hits.hits" 배열에서 "_source" 필드를 추출하여 매핑
//            JsonNode hits = rootNode.path("hits").path("hits");
//            for (JsonNode hit : hits) {
//                JsonNode source = hit.path("_source");
//                E result = objectMapper.treeToValue(source, clazz);
//                results.add(result);
//            }
//        } catch (Exception e) {
//            System.out.println("JSON 매핑 오류: " + e.getMessage());
//        }
//        return results;
//    }
//
//
//}
