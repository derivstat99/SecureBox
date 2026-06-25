package com.octral.SecureBox.service;

import com.octral.SecureBox.dto.SummaryResponse;
import com.octral.SecureBox.model.FileMetaData;
import com.octral.SecureBox.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DocumentSummaryService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";

    @Autowired
    private FileService fileService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private PdfTextExtractorService pdfTextExtractorService;

    @Autowired
    private GeminiService geminiService;

    @Value("${gemini.max-input-chars}")
    private int maxInputChars;

    public SummaryResponse summarizePdf(Long fileId, User user) throws IOException {
        FileMetaData file = fileService.getOwnedFile(fileId, user);

        if (file.isDeleted()) {
            throw new RuntimeException("Cannot summarize a file that is in trash");
        }

        if (!PDF_CONTENT_TYPE.equalsIgnoreCase(file.getFileType())) {
            throw new RuntimeException("Only PDF files can be summarized");
        }

        byte[] pdfBytes = cloudinaryService.download(file.getFilePath());
        String extractedText = pdfTextExtractorService.extractText(pdfBytes);

        boolean truncated = false;
        if (extractedText.length() > maxInputChars) {
            extractedText = extractedText.substring(0, maxInputChars);
            truncated = true;
        }

        String summary = geminiService.summarizeText(extractedText);

        return new SummaryResponse(file.getId(), file.getFileName(), summary, truncated);
    }
}
