package com.octral.SecureBox.service;

import com.octral.SecureBox.dto.gemini.GeminiRequest;
import com.octral.SecureBox.dto.gemini.GeminiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class GeminiService {

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent";

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiService(
            RestClient.Builder restClientBuilder,
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model
    ) {
        this.restClient = restClientBuilder.build();
        this.apiKey = apiKey;
        this.model = model;
    }

    public String summarizeText(String documentText) {
        String prompt = buildPrompt(documentText);
        GeminiRequest requestBody = GeminiRequest.fromPrompt(prompt);

        try {
            GeminiResponse response = restClient.post()
                    .uri(GEMINI_URL + "?key={apiKey}", model, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response == null) {
                throw new RuntimeException("Gemini returned an empty response");
            }

            return response.extractSummaryText();
        } catch (RestClientException ex) {
            throw new RuntimeException("Failed to call Gemini API: " + ex.getMessage(), ex);
        }
    }

    private String buildPrompt(String documentText) {
        return """
                You are a helpful assistant that summarizes documents clearly and accurately.
                Read the document text below and produce a concise summary with:
                1. A one-sentence overview
                2. Three to five bullet points of key ideas
                3. Any important names, dates, or numbers if present

                Keep the summary under 200 words.
                Do not invent facts that are not in the document.

                Document text:
                """ + documentText;
    }
}
