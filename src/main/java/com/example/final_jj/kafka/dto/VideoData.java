package com.example.final_jj.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class VideoData {
    private String category;
    private List<VideoItem> items;

    @Data
    public static class VideoItem {
        @JsonProperty("videoId")
        private String videoId;

        @JsonProperty("categoryId")
        private String categoryId;
    }
}
