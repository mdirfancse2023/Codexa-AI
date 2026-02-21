package com.lobvable.LovableApp.dto.chat;

import com.lobvable.LovableApp.entity.ChatEvent;
import com.lobvable.LovableApp.entity.ChatSession;
import com.lobvable.LovableApp.enums.MessageRole;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
        String content,
        String toolCalls,
        Integer tokensUsed,
        Instant createdAt,
        MessageRole role,
        List<ChatEventResponse>events
) {
}
