package org.quochung.hcm202.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.quochung.hcm202.dto.request.ChatRequest;
import org.quochung.hcm202.service.AIChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("ingest")
    public String ingest(@RequestBody String content) {
        chatService.ingestData(content);
        return "Đã nạp tài liệu vào bộ nhớ AI thành công";
    }
}
