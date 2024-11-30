package com.example.final_jj.elasticsearch.dto;

public class WordFrequency {
    private String word;
    private long count;

    public WordFrequency(String word, long count) {
        this.word = word;
        this.count = count;
    }

}
