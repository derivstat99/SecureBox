package com.octral.SecureBox.dto.gemini;

import lombok.Data;

import java.util.List;

@Data
public class GeminiResponse {

    private List<Candidate> candidates;

    public String extractSummaryText() {
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("Gemini returned no summary candidates");
        }
        Content content = candidates.get(0).getContent();
        if (content == null || content.getParts() == null || content.getParts().isEmpty()) {
            throw new RuntimeException("Gemini returned an empty summary");
        }
        return content.getParts().get(0).getText();
    }

    @Data
    public static class Candidate {
        private Content content;
    }

    @Data
    public static class Content {
        private List<Part> parts;
    }

    @Data
    public static class Part {
        private String text;
    }
}
