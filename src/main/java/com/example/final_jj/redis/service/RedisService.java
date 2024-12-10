package com.example.final_jj.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.example.final_jj.postgreSQL.repository.VideoIdRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VideoIdRepository repository;

    // Redis에서 데이터 가져오기
    public String getVideoData(String videoId) {

        return redisTemplate.opsForValue().get(videoId); // Redis에서 데이터 조회
    }

    public String getVideoDataAsJson(String videoId) throws JsonProcessingException {
        Map<Object, Object> videoData = redisTemplate.opsForHash().entries(videoId);
        return new ObjectMapper().writeValueAsString(videoData); // Jackson ObjectMapper로 JSON 변환
    }

    public List<String> getVideoIdHashFromRedis() throws JsonProcessingException {
        List<String> videoIds = repository.findAllVideoIds();
        List<String> videoData = new ArrayList<>();

        for (String videoId : videoIds) {
            videoData.add(getVideoDataAsJson(videoId));
        }

        return videoData;
    }

}
