//package com.example.final_jj.kafka.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//public class KafkaConsumer {
//    @KafkaListener(topics = "categoryLiveList", groupId = "LiveControlPod")
//    public void consume(String message) {
//        log.info("수신: {}", message);
//    }
//}