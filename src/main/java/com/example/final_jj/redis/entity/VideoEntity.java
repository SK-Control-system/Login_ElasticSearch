package com.example.final_jj.redis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "video_table")
public class VideoEntity {

    @Id
    private String videoid;

}
