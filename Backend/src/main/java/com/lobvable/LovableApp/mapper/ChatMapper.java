package com.lobvable.LovableApp.mapper;

import com.lobvable.LovableApp.dto.chat.ChatResponse;
import com.lobvable.LovableApp.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    List<ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessageList);
}
