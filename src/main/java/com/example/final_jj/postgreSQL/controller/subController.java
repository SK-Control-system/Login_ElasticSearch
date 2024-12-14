package com.example.final_jj.postgreSQL.controller;

import com.example.final_jj.postgreSQL.service.SubService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/sub")
@RestController
public class subController {

    @Autowired
    private SubService subService;

    // userId를 기준으로 채널 ID 리스트 반환
    @GetMapping("/channel/card")
    public ResponseEntity<List<String>> getSubscribedChannels(@RequestParam Long userId) {
        if (userId == null)
            return ResponseEntity.badRequest().build();

        List<String> channelIds = subService.getSubChannelIds(userId);
        return ResponseEntity.ok(channelIds);
    }
}
