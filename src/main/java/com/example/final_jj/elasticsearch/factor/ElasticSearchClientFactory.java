package com.example.final_jj.elasticsearch.factor;

import lombok.Getter;
@Getter
public class ElasticSearchClientFactory extends ElasticSearchClientFactoryConfig {
    public ElasticSearchClientFactory(String pathInfo) {
        super(pathInfo);
    }
}