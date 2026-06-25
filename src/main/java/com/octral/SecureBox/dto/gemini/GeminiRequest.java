package com.octral.SecureBox.dto.gemini;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class GeminiRequest {

    private List<Content> contents;

    public static GeminiRequest fromPrompt(String prompt) {
        GeminiRequest request = new GeminiRequest();
        request.setContents(List.of(new Content(List.of(new Part(prompt)))));
        return request;
    }

    @Data
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Data
    @AllArgsConstructor
    public static class Part {
        private String text;
    }
}
