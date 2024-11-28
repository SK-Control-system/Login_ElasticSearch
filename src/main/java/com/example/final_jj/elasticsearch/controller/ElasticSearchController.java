package com.example.final_jj.elasticsearch.controller;

import com.example.final_jj.elasticsearch.entity.MyDocument;
import com.example.final_jj.elasticsearch.enums.HttpMethodEnum;
import com.example.final_jj.elasticsearch.factor.ElasticSearchClientFactory;
import com.example.final_jj.elasticsearch.utils.common.ElasticExecutor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/elasticsearch")
public class ElasticSearchController {
    private final RestClient client;

    public ElasticSearchController(@Value("${elasticsearch.config.path}") String pathInfo) {
        this.client = new ElasticSearchClientFactory(pathInfo).getEsClient();
    }
}