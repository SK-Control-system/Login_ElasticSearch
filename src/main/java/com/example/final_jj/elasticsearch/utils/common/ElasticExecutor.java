package com.example.final_jj.elasticsearch.utils.common;

import com.example.final_jj.elasticsearch.enums.HttpMethodEnum;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface ElasticExecutor {
     static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * Elasticsearch에서 전체 데이터를 페이지 단위로 조회합니다.
     * @param client RestClient 인스턴스
     * @param index 검색할 인덱스 이름
     * @param method HTTP 메소드
     * @param queryJson 검색 쿼리 JSON 문자열
     * @return 쿼리 데이터 사이즈 만큼
     */
    static <E> E search(RestClient client, String index, HttpMethodEnum method, String queryJson, Class<E> clazz) {
        try {
            Request request = new Request(method.getMethod(), "/" + index + "/_search");
            request.addParameter("pretty", "true");
            request.setJsonEntity(queryJson);

            // 요청 실행
            Response response = client.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity());

            return  parseSingleResult(responseBody, clazz);
        }catch (Exception e){
            System.out.println("ES ERROR : " + e.getMessage());
            return null;
        }
    }

    /**
     * Elasticsearch에서 전체 데이터를 페이지 단위로 조회합니다.
     * @param client RestClient 인스턴스
     * @param index 검색할 인덱스 이름
     * @param method HTTP 메소드
     * @param queryJson 검색 쿼리 JSON 문자열
     * @return 쿼리 데이터 사이즈 만큼
     */
    static <E> List<E> searchList(RestClient client, String index, HttpMethodEnum method, String queryJson, Class<E> clazz) {
        List<E> results = new ArrayList<>();
        try {
                Request request = new Request(method.getMethod(), "/" + index + "/_search");
                request.addParameter("pretty", "true");
                request.setJsonEntity(queryJson);

                // 요청 실행
                Response response = client.performRequest(request);
                String responseBody = EntityUtils.toString(response.getEntity());

                // 응답에서 검색 결과 추가
                List<E> pageResults = parseResults(responseBody, clazz);
                results.addAll(pageResults);

        }catch (Exception e){
            System.out.println("ES ERROR : " + e.getMessage());
        }
        return (List<E>) results;
    }

    /**
     * Elasticsearch에서 전체 데이터를 페이지 단위로 조회합니다.
     * @param client RestClient 인스턴스
     * @param index 검색할 인덱스 이름
     * @param method HTTP 메소드
     * @param queryJson 검색 쿼리 JSON 문자열
     * @return 전체 데이터 목록
     */
    static <E> List<E> searchAfterList(RestClient client, String index, HttpMethodEnum method, String queryJson, Class<E> clazz) {
        List<E> results = new ArrayList<>();
        List<String> searchAfter = null;
        try {
            while (true) {
                Request request = new Request(method.getMethod(), "/" + index + "/_search");
                request.addParameter("pretty", "true");
                request.setJsonEntity(buildQueryWithSearchAfter(queryJson, searchAfter));

                // 요청 실행
                Response response = client.performRequest(request);
                String responseBody = EntityUtils.toString(response.getEntity());

                // 응답에서 검색 결과 추가
                List<E> pageResults = parseResults(responseBody, clazz);
                results.addAll(pageResults);

                // 마지막 결과에서 다음 페이지를 위한 sort 값을 추출
                searchAfter = getLastSortValue(responseBody);

                // 더 이상 결과가 없으면 종료
                if (searchAfter == null) {
                    break;
                }
            }
        }catch (Exception e){
            System.out.println("ES ERROR : " + e.getMessage());
        }
        return (List<E>) results;
    }





    /**
     * search_after 파라미터를 포함하여 쿼리 JSON을 생성합니다.
     *
     * @param queryJson   기본 쿼리 JSON
     * @param searchAfter 이전 페이지의 마지막 sort 값
     * @return search_after 파라미터가 포함된 쿼리 JSON
     * @throws Exception JSON 파싱 예외
     */
    private static String buildQueryWithSearchAfter(String queryJson, List<String> searchAfter) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        // 기존 queryJson을 JsonNode로 파싱
        JsonNode queryNode = objectMapper.readTree(queryJson);

        // search_after를 추가
        ObjectNode queryObject = (ObjectNode) queryNode;

        // searchAfterValues가 비어있지 않은 경우
        if (searchAfter != null && !searchAfter.isEmpty()) {
            ArrayNode searchAfterArray = objectMapper.createArrayNode();

            // 모든 search_after 값을 배열에 추가
            for (String value : searchAfter) {
                searchAfterArray.add(value);
            }

            // queryNode에 search_after 추가
            queryObject.set("search_after", searchAfterArray);
        }

        // 결과 JSON을 문자열로 변환하여 반환
        return objectMapper.writeValueAsString(queryObject);
    }

    /**
     * 응답 JSON에서 마지막 sort 값을 추출합니다.
     * @param responseBody 응답 JSON 문자열
     * @return 마지막 sort 값
     * @throws IOException JSON 파싱 오류
     */
    private static List<String> getLastSortValue(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode hitsNode = rootNode.path("hits").path("hits");

        if (hitsNode.isArray() && hitsNode.size() > 0) {
            JsonNode lastHitNode = hitsNode.get(hitsNode.size() - 1); // 마지막 결과
            JsonNode sortNode = lastHitNode.path("sort"); // sort 필드

            if (sortNode.isArray() && sortNode.size() > 0) {
                List<String> sortValues = new ArrayList<>();
                for (JsonNode sortValueNode : sortNode) {
                    sortValues.add(sortValueNode.asText());
                }
                return sortValues;  // 모든 sort 값을 리스트로 반환
            }
        }

        return null;
    }

    // 클라이언트 종료를 위한 별도 메소드
    private static void closeClient(RestClient client) {
        try {
            //client.close();
        } catch (Exception e) {
            System.out.println("클라이언트 종료 중 오류 발생");
        }
    }

    private static <E> List<E> parseResults(String responseBody, Class<E> clazz) throws Exception {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode hitsNode = rootNode.path("hits").path("hits");
        List<E> results = new ArrayList<>();

        for (JsonNode hitNode : hitsNode) {
            JsonNode sourceNode = hitNode.path("_source");
            E result = objectMapper.treeToValue(sourceNode, clazz);
            results.add(result);
        }

        return results;
    }

    public static <E> E parseSingleResult(String responseBody, Class<E> clazz) throws IOException {
        // JSON 응답을 JsonNode로 변환
        JsonNode rootNode = objectMapper.readTree(responseBody);
        JsonNode hitsNode = rootNode.path("hits").path("hits");

        // hitsNode가 빈 배열인지 확인
        if (hitsNode.isArray() && hitsNode.size() == 0) {
            return null; // 결과가 없으면 null 반환
        }

        // 첫 번째 결과 추출
        JsonNode firstHitNode = hitsNode.get(0).path("_source");

        // 첫 번째 결과를 지정된 클래스 타입으로 변환
        return objectMapper.treeToValue(firstHitNode, clazz);
    }

}