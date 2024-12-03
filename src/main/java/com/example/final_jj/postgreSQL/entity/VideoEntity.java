package com.example.final_jj.postgreSQL.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;

@Entity
@Data
@Table(name = "video_table")
public class VideoEntity {

    @Id
    private String videoid;

    public String getVideoid() {
        return videoid;
    }

}
