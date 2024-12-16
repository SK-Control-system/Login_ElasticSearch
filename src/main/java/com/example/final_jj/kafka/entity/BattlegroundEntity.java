package com.example.final_jj.kafka.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "battleground_table")
@Getter
@Setter
public class BattlegroundEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "videoid", nullable = false)
    private String videoId;
}
