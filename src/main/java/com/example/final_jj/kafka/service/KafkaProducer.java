package com.example.final_jj.kafka.service;

import com.example.final_jj.postgreSQL.repository.SubscribeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class KafkaProducer {
    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String YOUTUBE_API_URL =
            "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=%s&eventType=live&type=video&key=%s";
    private static final String API_KEY = "AIzaSyAk61e0vXHzb_d8kIegfJpsUUY6YXD8oV4";


    public List<String> getAllChannelIds() {
        return subscribeRepository.findAllChannelIds();
    }

    // 채널 ID 리스트를 기반으로 YouTube API 호출 후 Kafka로 전송
    public void fetchLiveVideosAndSend() {
        List<String> channelIds = getAllChannelIds();

        if (channelIds == null || channelIds.isEmpty()) {
            System.out.println("구독한 채널이 없습니다.");
            return;
        }

        RestTemplate restTemplate = new RestTemplate();

        for (String channelId : channelIds) {
            try {
                // YouTube API 호출
                String apiUrl = String.format(YOUTUBE_API_URL, channelId, API_KEY);
                String response = restTemplate.getForObject(apiUrl, String.class);

                // Kafka로 데이터 전송
                kafkaTemplate.send("subChannelId", response);

                // 로그 출력
                System.out.println("채널 ID: " + channelId);
                System.out.println("API 응답 데이터: " + response);
            } catch (Exception e) {
                System.err.println("API 호출 실패 - 채널 ID: " + channelId);
                e.printStackTrace();
            }
        }
    }

//    @Scheduled(fixedRate = 300000) // 5분마다 실행 (300000 밀리초)
//    public void scheduleFetchLiveVideos() {
//        System.out.println("5분마다 모든 채널 ID 가져와서 YouTube API 호출 시작...");
//
//        try {
//            fetchLiveVideosAndSend(); // 채널 ID 리스트 기반 API 호출
//        } catch (Exception e) {
//            System.err.println("스케줄러 실행 중 오류 발생:");
//            e.printStackTrace();
//        }
//    }
}
