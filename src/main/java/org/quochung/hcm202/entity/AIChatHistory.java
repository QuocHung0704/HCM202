package org.quochung.hcm202.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "AI_chat_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String userMessage;

    @Column(columnDefinition = "TEXT")
    private String aiResponse;

    @Column(columnDefinition = "TEXT")
    private String retrievedContext;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}

