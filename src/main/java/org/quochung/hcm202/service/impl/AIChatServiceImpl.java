package org.quochung.hcm202.service.impl;

import lombok.RequiredArgsConstructor;
import org.quochung.hcm202.dto.request.ChatRequest;
import org.quochung.hcm202.entity.AIChatHistory;
import org.quochung.hcm202.repository.AIChatHistoryRepository;
import org.quochung.hcm202.service.AIChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIChatServiceImpl implements AIChatService {
    private final ChatClient chatClient;
    private final PgVectorStore vectorStore;
    private final AIChatHistoryRepository chatHistoryRepository;

    public AIChatServiceImpl(ChatClient.Builder builder, PgVectorStore vectorStore, AIChatHistoryRepository chatHistoryRepository) {
        chatClient = builder.build();
        this.vectorStore = vectorStore;
        this.chatHistoryRepository = chatHistoryRepository;
    }

    public String chat(ChatRequest request, String ipAddress) {
        List<Document> similarDocuments = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(request.message())
                        .topK(2)
                        .build());

        String context = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        String aiResponse = chatClient.prompt()
                .system(s -> s.text("Bạn là một chuyên gia hỗ trợ học tập môn Tư tưởng Hồ Chí Minh. " +
                                "NHIỆM VỤ QUAN TRỌNG: " +
                                "1. CHỈ sử dụng ngữ cảnh được cung cấp dưới đây để trả lời câu hỏi. " +
                                "2. Nếu câu hỏi không liên quan đến Tư tưởng Hồ Chí Minh hoặc ngữ cảnh không có thông tin, " +
                                "hãy lịch sự trả lời: 'Xin lỗi, tôi chỉ hỗ trợ các kiến thức liên quan đến môn Tư tưởng Hồ Chí Minh.' " +
                                "3. Tuyệt đối không trả lời bất kỳ chủ đề ngoại lai nào khác.\n\n" +
                                "Ngữ cảnh: {context}")
                        .param("context", context))
                .user(request.message())
                .call()
                .content();

        // 3. Lưu dữ liệu chat vào ChatHistory
        AIChatHistory history = AIChatHistory.builder()
                .ipAddress(ipAddress)
                .userMessage(request.message())
                .aiResponse(aiResponse)
                .retrievedContext(context)
                .createdAt(LocalDateTime.now())
                .build();
        chatHistoryRepository.save(history);

        return aiResponse;
    }

    @Override
    public void ingestData(String content) {
        Document doc = new Document(content);
        vectorStore.add(List.of(doc));
    }
}
