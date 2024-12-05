package com.example.final_jj.postgreSQL.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Data
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // 리포트 ID (Primary Key)

    @Column(name ="videoid", nullable = false, length = 100)
    private String videoId; // 비디오 ID

    @Column(length = 50)
    private String concurrentviewers; // 동시 시청자 수

    @Column(length = 50)
    private String likecount; // 좋아요 수

    @Column(length = 100)
    private String videotitle; // 비디오 제목

    @Column
    private LocalDateTime actualstarttime; // 실제 방송 시작 시간

    @Column(length = 500)
    private String videothumbnailurl; // 비디오 썸네일 URL

    @Column(length = 100)
    private String channeltitle; // 채널 제목

    @Column(name="channelid", nullable = false, length = 100)
    private String channelId; // 채널 ID

    @Column(name="created_at")
    private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시간

}