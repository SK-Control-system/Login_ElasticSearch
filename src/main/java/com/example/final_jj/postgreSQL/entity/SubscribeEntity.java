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
    @Column(name = "sub_id") // 컬럼명 명시
    private Long subId;

    @Column(name = "user_id", nullable = false) // 컬럼명 명시
    private Long userId;

    @Column(name = "channel_id", length = 500) // 컬럼명 명시
    private String channelId;

    @Column(name = "sub_at", nullable = false) // 컬럼명 명시
    private LocalDateTime subAt;
}
