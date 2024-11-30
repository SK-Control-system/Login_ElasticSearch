package com.example.final_jj.elasticsearch.factor;


import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.http.Header;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.hosts}")
    private List<String> hosts;

    @Value("${elasticsearch.socketTimeout}")
    private int socketTimeout;

    @Value("${elasticsearch.connectionTimeout}")
    private int connectionTimeout;

    @Value("${elasticsearch.maxConnTotal}")
    private int maxConnTotal;

    @Value("${elasticsearch.maxConnPerRoute}")
    private int maxConnPerRoute;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // hosts 값을 HttpHost로 변환
        HttpHost[] httpHosts = hosts.stream()
                .map(host -> new HttpHost(host.split(":")[0], Integer.parseInt(host.split(":")[1])))
                .toArray(HttpHost[]::new);

        // 기본 HTTP 헤더 설정 (Connection: Keep-Alive)
        Header[] defaultHeaders = new Header[] {
                new BasicHeader("Connection", "Keep-Alive")
        };

        return new RestHighLevelClient(
                RestClient.builder(httpHosts)
                        .setDefaultHeaders(defaultHeaders)  // Header[] 배열을 사용
                        .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                                .setConnectTimeout(connectionTimeout)
                                .setSocketTimeout(socketTimeout))
                        .setHttpClientConfigCallback(httpClientBuilder ->
                                httpClientBuilder.setMaxConnTotal(maxConnTotal)
                                        .setMaxConnPerRoute(maxConnPerRoute)
                        )
        );
    }
}
