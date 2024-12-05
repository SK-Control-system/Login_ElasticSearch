package com.example.final_jj.postgreSQL.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String videoId;
    private String channelId;
    private LocalDateTime createdAt;

}