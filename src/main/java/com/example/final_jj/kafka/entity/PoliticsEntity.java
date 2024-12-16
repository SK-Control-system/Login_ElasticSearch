package com.example.final_jj.kafka.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "politics_table")
@Getter
@Setter
public class PoliticsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "videoid", nullable = false)
    private String videoId;
}
