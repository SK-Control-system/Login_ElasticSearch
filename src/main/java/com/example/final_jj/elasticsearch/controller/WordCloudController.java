package com.example.final_jj.elasticsearch.controller;

import com.example.final_jj.elasticsearch.dto.WordFrequency;
import com.example.final_jj.elasticsearch.service.Impl.WordCloudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wordcloud")
public class WordCloudController {

    @Autowired
    private WordCloudService wordCloudService;

    @GetMapping("/get")
    public List<WordFrequency> getWordCloud() {
        return wordCloudService.getTopKeywords();
    }
}
