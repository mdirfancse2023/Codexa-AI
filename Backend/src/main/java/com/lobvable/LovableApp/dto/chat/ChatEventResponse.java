package com.lobvable.LovableApp.dto.chat;

import com.lobvable.LovableApp.entity.ChatMessage;
import com.lobvable.LovableApp.enums.ChatEventType;
import jakarta.persistence.*;

public record ChatEventResponse(
        Long id,
        Integer sequenceOrder,
        ChatEventType type,
        String content,
        String filePath,
        String metadata
) {
}
