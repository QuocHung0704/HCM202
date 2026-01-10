package org.quochung.hcm202.service;

import org.quochung.hcm202.dto.request.ChatRequest;

public interface AIChatService {
    String chat(ChatRequest request);

}
