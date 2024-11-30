package com.example.final_jj.elasticsearch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "chatting_youtube_2024")
public class ChatMessage {

    @Id
    private String id;

    private String message;

    private String videoId;

    // Getters and Setters
}
