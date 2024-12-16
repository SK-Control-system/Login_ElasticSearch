package com.example.final_jj.postgreSQL.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "ended_live")
@Data
public class EndedLive {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ended_live_seq")
    @SequenceGenerator(name = "ended_live_seq", sequenceName = "ended_live_details_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "channel_id", nullable = false, length = 50)
    private String channelId;

    @Column(name = "channel_title", length = 100)
    private String channelTitle;

    @Column(name = "channel_thumbnail_uri", length = 150)
    private String channelThumbnailUri;

    @Column(name = "video_id", nullable = false, length = 50, unique = true)
    private String videoId;

    @Column(name = "video_title", length = 100)
    private String videoTitle;

    @Column(name = "video_thumbnail_uri", length = 150)
    private String videoThumbnailUri;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;
}