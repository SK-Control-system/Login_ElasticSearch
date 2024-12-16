package com.example.final_jj.channelReport.controller;

import com.example.final_jj.channelReport.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    private ReportService reportService;


    @GetMapping("/month/viewer")
    public List<Map<String, Object>> getViewer(@RequestParam String channelId) {
        return reportService.searchMonthlyStats(channelId);
    }
}
