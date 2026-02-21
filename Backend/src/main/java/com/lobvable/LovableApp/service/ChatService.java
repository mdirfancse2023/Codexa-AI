package com.lobvable.LovableApp.service;

import com.lobvable.LovableApp.dto.chat.ChatResponse;

import java.util.List;

public interface ChatService {
    List<ChatResponse> getProjectChatHistory(Long projectId);
}
