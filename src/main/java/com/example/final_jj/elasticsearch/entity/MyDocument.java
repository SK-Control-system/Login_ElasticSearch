package com.example.final_jj.elasticsearch.entity;

import java.util.Map;

public class MyDocument {
    private String chatterName; // 단순 문자열 필드
    private ChattingAnalysisResult chattingAnalysisResult; // 중첩 객체로 매핑

    // Getters and Setters
    public String getChatterName() {
        return chatterName;
    }

    public void setChatterName(String chatterName) {
        this.chatterName = chatterName;
    }

    public ChattingAnalysisResult getChattingAnalysisResult() {
        return chattingAnalysisResult;
    }

    public void setChattingAnalysisResult(ChattingAnalysisResult chattingAnalysisResult) {
        this.chattingAnalysisResult = chattingAnalysisResult;
    }

    @Override
    public String toString() {
        return "MyDocument{" +
                "chatterName='" + chatterName + '\'' +
                ", chattingAnalysisResult=" + chattingAnalysisResult +
                '}';
    }

    public static class ChattingAnalysisResult {
        private Sentiment sentiment; // 중첩 객체로 매핑
        private Emotion emotion; // 중첩 객체로 매핑

        // Getters and Setters
        public Sentiment getSentiment() {
            return sentiment;
        }

        public void setSentiment(Sentiment sentiment) {
            this.sentiment = sentiment;
        }

        public Emotion getEmotion() {
            return emotion;
        }

        public void setEmotion(Emotion emotion) {
            this.emotion = emotion;
        }

        @Override
        public String toString() {
            return sentiment + "" + emotion;
        }
    }

    // 내부 클래스 Sentiment와 Emotion은 기존과 동일
    public static class Sentiment {
//        private Scores scores; // 내부 객체
//        private double confidence;
        private String label;

        // Getters and Setters

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public static class Emotion {
        private String label;

        // Getters and Setters

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

//    public static class Scores {
//        private double negative;
//        private double neutral;
//        private double positive;
//
//        // Getters and Setters
//        public double getNegative() {
//            return negative;
//        }
//
//        public void setNegative(double negative) {
//            this.negative = negative;
//        }
//
//        public double getNeutral() {
//            return neutral;
//        }
//
//        public void setNeutral(double neutral) {
//            this.neutral = neutral;
//        }
//
//        public double getPositive() {
//            return positive;
//        }
//
//        public void setPositive(double positive) {
//            this.positive = positive;
//        }
//
//        @Override
//        public String toString() {
//            return "Scores{" +
//                    "negative=" + negative +
//                    ", neutral=" + neutral +
//                    ", positive=" + positive +
//                    '}';
//        }
//    }
}
