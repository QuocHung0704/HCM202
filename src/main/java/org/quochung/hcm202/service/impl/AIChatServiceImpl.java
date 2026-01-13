package org.quochung.hcm202.service.impl;

import lombok.RequiredArgsConstructor;
import org.quochung.hcm202.dto.request.ChatRequest;
import org.quochung.hcm202.entity.AIChatHistory;
import org.quochung.hcm202.repository.AIChatHistoryRepository;
import org.quochung.hcm202.service.AIChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
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
//                        .similarityThreshold(0.7)
                        .build());

        String context = similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        String aiResponse = chatClient.prompt()
                .system(s -> s.text("Bạn là chuyên gia hỗ trợ học tập môn Tư tưởng Hồ Chí Minh.\n" +
                                "QUY TẮC TRẢ LỜI CỰC KỲ NGHIÊM NGẶT:\n" +
                                "1. CHỈ sử dụng thông tin từ 'Ngữ cảnh' dưới đây để trả lời.\n" +
                                "2. Nếu câu hỏi KHÔNG có trong 'Ngữ cảnh' hoặc 'Ngữ cảnh' trống, hãy trả lời chính xác câu: 'Xin lỗi, tôi không tìm thấy thông tin này trong tài liệu học tập môn Tư tưởng Hồ Chí Minh.'\n" +
                                "3. Tuyệt đối không sử dụng kiến thức bên ngoài hoặc kiến thức chung của bạn.\n" +
                                "4. Không trả lời các chủ đề khác ngoài Tư tưởng Hồ Chí Minh.\n\n" +
                                "Ngữ cảnh:\n{context}")
                        .param("context", context))
                .user(request.message())
                .call()
                .content();

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
        if (content == null || content.isBlank()) return;
        String sanitizedContent = content.replace("\u0000", "");

        Document fullDoc = new Document(sanitizedContent);

        // TokenTextSplitter giúp cắt file lớn thành các đoạn nhỏ có nghĩa
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.split(List.of(fullDoc));
        vectorStore.add(chunks);
    }
}
