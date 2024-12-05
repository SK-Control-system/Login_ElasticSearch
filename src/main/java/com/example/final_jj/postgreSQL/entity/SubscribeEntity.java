package com.example.final_jj.postgreSQL.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscribe")
public class SubscribeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subId;

    private Long userId;
    private String channelId;

    private LocalDateTime subAt;

}