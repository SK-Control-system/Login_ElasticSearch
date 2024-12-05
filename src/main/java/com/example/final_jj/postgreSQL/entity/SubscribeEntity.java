package com.example.final_jj.postgreSQL.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscribe")
@Data
public class SubscribeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subId; // 구독 ID (Primary Key)

    @Column(nullable = false)
    private Long userId; // 사용자 ID (Foreign Key가 아닌 값)

    @Column(length = 500)
    private String channelId; // 채널 ID

    @Column(nullable = false)
    private LocalDateTime subAt; // 구독 시간

}