package com.example.final_jj.kafka.controller;


import com.example.final_jj.kafka.service.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/kafka")
@RestController
@Slf4j
public class KafkaController {
    private final KafkaProducer producer;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping
    public String test() {
        for (int i=0; i<10; i++) {
            log.debug("log~~log~~log~~ {}", i);
            log.info("~~~~LOG~~~~ {}", i);

            // 커스텀 로거 사용
            Log logger_info = LogFactory.getLog("INFO_LOG");
            logger_info.debug("로그입니다~~~ " + i);
        }
        return "Hello!";
    }


    @PostMapping
    public String sendMessage(@RequestParam("message") String message) {
        this.producer.sendMessage(message);
        return message;
    }

    @GetMapping("/123")
    public String getMessage() {
        return this.kafkaTemplate.getDefaultTopic();
    }
}