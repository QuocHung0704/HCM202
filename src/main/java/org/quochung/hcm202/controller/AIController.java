package org.quochung.hcm202.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.quochung.hcm202.dto.request.ChatRequest;
import org.quochung.hcm202.service.AIChatService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIChatService chatService;

    @PostMapping("/chat")
    String chat(@RequestBody ChatRequest request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = httpRequest.getRemoteAddr();
        }
        return chatService.chat(request, ipAddress);
    }

    @PostMapping(value = "/ingest", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public String ingest(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String content = "";

        if (fileName != null && fileName.endsWith(".docx")) {
            // Xử lý tệp Word (.docx) bằng Apache POI
            try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                content = extractor.getText();
            }
        } else {
            // Xử lý tệp văn bản thuần túy (.txt)
            content = new String(file.getBytes(), StandardCharsets.UTF_8);
        }

        if (content.isBlank()) {
            return "Tệp trống hoặc không thể đọc nội dung.";
        }

        chatService.ingestData(content);
        return "Đã nạp tài liệu từ file " + fileName + " thành công";
    }
}
