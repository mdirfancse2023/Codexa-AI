package com.lobvable.LovableApp.service.impl;

import com.lobvable.LovableApp.entity.ChatMessage;
import com.lobvable.LovableApp.entity.ChatSession;
import com.lobvable.LovableApp.entity.ChatSessionId;
import com.lobvable.LovableApp.mapper.ChatMapper;
import com.lobvable.LovableApp.repository.ChatMessageRepository;
import com.lobvable.LovableApp.repository.ChatSessionRepository;
import com.lobvable.LovableApp.security.AuthUtil;
import com.lobvable.LovableApp.service.ChatService;
import com.lobvable.LovableApp.dto.chat.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final AuthUtil authUtil;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMapper chatMapper;

    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionId(projectId, userId)
        );

        List<ChatMessage> chatMessageList = chatMessageRepository.findByChatSession(chatSession);
        return chatMapper.fromListOfChatMessage(chatMessageList);
    }
}
