package com.example.final_jj.postgreSQL.service;

import com.example.final_jj.postgreSQL.repository.SubscribeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubService {

    @Autowired
    private SubscribeRepository subscribeRepository;

    public List<String> getSubChannelIds(Long userId) {
        return subscribeRepository.findChannelIdsByUserId(userId);
    }
}
