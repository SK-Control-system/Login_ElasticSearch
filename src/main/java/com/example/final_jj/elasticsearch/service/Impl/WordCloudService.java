package com.example.final_jj.elasticsearch.service.Impl;

import com.example.final_jj.elasticsearch.dto.WordFrequency;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class WordCloudService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public List<WordFrequency> getTopKeywords() {
        SearchRequest searchRequest = new SearchRequest("chatting_youtube_2024*");
        searchRequest.source().query(QueryBuilders.matchAllQuery());

        // 집계 추가
        searchRequest.source().aggregation(AggregationBuilders.terms("top_keywords").field("message").size(50));

        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Aggregation aggregation = response.getAggregations().get("top_keywords");
            Terms terms = (Terms) aggregation;

            List<WordFrequency> wordFrequencies = new ArrayList<>();
            for (Terms.Bucket bucket : terms.getBuckets()) {
                String word = bucket.getKeyAsString();
                long count = bucket.getDocCount();
                wordFrequencies.add(new WordFrequency(word, count));
            }
            return wordFrequencies;
        } catch (IOException e) {
            // Handle exceptions
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
