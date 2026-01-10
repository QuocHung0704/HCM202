package org.quochung.hcm202.service.impl;

import org.quochung.hcm202.dto.request.ChatRequest;
import org.quochung.hcm202.service.AIChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIChatServiceImpl implements AIChatService {
    private final ChatClient chatClient;

    public AIChatServiceImpl(ChatClient.Builder builder) {
        chatClient = builder.build();
    }

    public String chat(ChatRequest request) {
        return chatClient
                .prompt(request.message())
                .call()
                .content();
    }
}
