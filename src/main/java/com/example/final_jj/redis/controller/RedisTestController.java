package com.example.final_jj.redis.controller;

import com.example.final_jj.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.final_jj.elasticsearch.utils.common.ElasticExecutor.objectMapper;

@RestController
public class RedisTestController {

    @Autowired
    private RedisService redisService;

    @GetMapping("/get")
    public String getDataFromRedis(@RequestParam String key) {
        try {
            // Redis에서 데이터 조회
            String data = redisService.getVideoData(key);
            if (data == null) {
                return "No data found for key: " + key;
            }
            // JSON 데이터를 Pretty 형식으로 변환
            Object jsonObject = objectMapper.readValue(data, Object.class);
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);

            return prettyJson;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while retrieving data.";
        }
    }

    @GetMapping("/get/videoId")
    public List<String> getVideoIdFromRedis() {
        List<String> videoData = redisService.getVideoIdFromRedis();
        return videoData;
    }

}
