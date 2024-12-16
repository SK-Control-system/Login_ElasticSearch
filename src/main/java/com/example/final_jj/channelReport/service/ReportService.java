package com.example.final_jj.channelReport.service;

import com.example.final_jj.elasticsearch.factor.ElasticSearchClientFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReportService(@Value("${elasticsearch.config.path}") String pathInfo) {
        this.restClient = new ElasticSearchClientFactory(pathInfo).getEsClient();
    }

    public List<Map<String, Object>> searchMonthlyStats(String channelId) {
        String searchQuery = "{"
                + "  \"size\": 0,"
                + "  \"query\": {"
                + "    \"bool\": {"
                + "      \"filter\": ["
                + "        {\"term\": {\"videoData.channelId\": \"" + channelId + "\"}}"
                + "      ]"
                + "    }"
                + "  },"
                + "  \"aggs\": {"
                + "    \"monthly_data\": {"
                + "      \"date_histogram\": {"
                + "        \"field\": \"videoData.actualStartTime\","
                + "        \"calendar_interval\": \"month\","
                + "        \"format\": \"yyyy-MM\""
                + "      },"
                + "      \"aggs\": {"
                + "        \"total_view_count\": {"
                + "          \"sum\": {\"field\": \"videoData.viewCount\"}"
                + "        },"
                + "        \"total_concurrent_viewers\": {"
                + "          \"sum\": {\"field\": \"videoData.concurrentViewers\"}"
                + "        },"
                + "        \"average_concurrent_viewers\": {"
                + "          \"avg\": {\"field\": \"videoData.concurrentViewers\"}"
                + "        }"
                + "      }"
                + "    }"
                + "  }"
                + "}";

        return searchSourceDocuments("video_youtube_*", searchQuery).stream()
                .map(bucket -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("month", bucket.get("key_as_string"));

                    // 안전하게 null 처리 및 값 추출
                    double totalViewers = safeExtractDouble(bucket, "total_concurrent_viewers");
                    double averageViewers = safeExtractDouble(bucket, "average_concurrent_viewers");
                    double totalViewCount = safeExtractDouble(bucket, "total_view_count");

                    result.put("total_concurrent_viewers", (long) totalViewers);
                    result.put("average_concurrent_viewers", (long) averageViewers);
                    result.put("total_view_count", (long) totalViewCount);

                    return result;
                })
                .collect(Collectors.toList());
    }


    private List<Map<String, Object>> searchSourceDocuments(String index, String searchQuery) {
        try {
            Request request = new Request("GET", "/" + index + "/_search");
            request.setJsonEntity(searchQuery);

            Response response = restClient.performRequest(request);
            Map<String, Object> responseBody = objectMapper.readValue(response.getEntity().getContent(), Map.class);

            Map<String, Object> aggregations = (Map<String, Object>) responseBody.get("aggregations");
            Map<String, Object> monthlyData = (Map<String, Object>) aggregations.get("monthly_data");

            return (List<Map<String, Object>>) monthlyData.get("buckets");
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch data from Elasticsearch", e);
        }
    }

    private double safeExtractDouble(Map<String, Object> bucket, String field) {
        Map<String, Object> fieldMap = (Map<String, Object>) bucket.getOrDefault(field, Map.of("value", 0.0));
        Object value = fieldMap.get("value");
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }

}
