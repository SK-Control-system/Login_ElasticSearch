package com.example.final_jj.redis.service;

import com.example.final_jj.postgreSQL.repository.VideoIdRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    //postgresSpl에 videoId 가져오기
    public List<String> getVideoIdFromRedis() {
        List<String> videoIds= repository.findAllVideoIds();
        List<String> videoData = new ArrayList<>();

        for(String videoId:videoIds) {
            videoData.add(getVideoData(videoId));
        }

        return videoData;
    }
}
