package com.example.final_jj.postgreSQL.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 PK
    @Column(name = "user_id") // 테이블의 user_id와 매핑
    private Long userId;
    @Column(name = "google_id") // google_id 컬럼과 매핑
    private String googleId;
    @Column(name = "yt_id") // yt_id 컬럼과 매핑
    private String ytId;
    @Column(name = "name") // name 컬럼과 매핑
    private String name;
    @Column(name = "profile_picture_url") // profile_picture_url 컬럼과 매핑
    private String profilePictureUrl;
    @Column(name = "access_token") // access_token 컬럼과 매핑
    private String accessToken;
    @Column(name = "refresh_token") // refresh_token 컬럼과 매핑
    private String refreshToken;
    @Column(name = "create_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP") // 생성 시 시간
    private LocalDateTime createdAt;
    @Column(name = "last_login_at", columnDefinition = "TIMESTAMP") // 마지막 로그인 시간
    private LocalDateTime lastLoginAt;
    @Column(name = "is_withdraw", nullable = false) // 기본값 false
    private Boolean isWithdraw = false;
    @Column(name = "tag_order", length = 255) // tag_order 컬럼과 매핑
    private String tagOrder;
    @Column(name = "channel_id", length = 500) // channel_id 컬럼과 매핑
    private String channelId;
}