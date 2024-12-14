package com.example.final_jj.kafka.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "video_table")
public class VideoIdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "videoid")
    private String videoId;

    @Column(name = "categoryid", nullable = false)
    private String categoryId;
}
