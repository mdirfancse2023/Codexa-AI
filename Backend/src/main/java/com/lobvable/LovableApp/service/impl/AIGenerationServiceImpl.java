package com.lobvable.LovableApp.service.impl;

import com.lobvable.LovableApp.entity.*;
import com.lobvable.LovableApp.enums.ChatEventType;
import com.lobvable.LovableApp.enums.MessageRole;
import com.lobvable.LovableApp.error.ResourceNotFoundException;
import com.lobvable.LovableApp.llm.LlmResponseParser;
import com.lobvable.LovableApp.llm.PromptUtils;
import com.lobvable.LovableApp.llm.advisors.FileTreeContextAdvisor;
import com.lobvable.LovableApp.llm.tools.CodeGenerationTools;
import com.lobvable.LovableApp.repository.*;
import com.lobvable.LovableApp.security.AuthUtil;
import com.lobvable.LovableApp.service.AIGenerationService;
import com.lobvable.LovableApp.service.ProjectFileService;
import com.lobvable.LovableApp.service.UsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.lobvable.LovableApp.dto.chat.StreamResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIGenerationServiceImpl implements AIGenerationService {
    private final ChatSessionRepository chatSessionRepository;
    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final LlmResponseParser llmResponseParser;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventRepository chatEventRepository;
    private final UsageService usageService;
    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);
    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<StreamResponse> streamResponse(String userMessage, Long projectId) {
        //usageService.checkDailyTokensUsage();
        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = createChatSessionIfNotExists(projectId, userId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );

        StringBuilder fullResponseBuffer = new StringBuilder();

        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService, projectId);

        AtomicReference<Long> startTime = new AtomicReference<>(System.currentTimeMillis());
        AtomicReference<Long> endTime = new AtomicReference<>(0L);
        AtomicReference<Usage> usageRef = new AtomicReference<>();

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT) // add system prompt
                .user(userMessage) // add user message
                .tools(codeGenerationTools)
                .advisors(advisorSpec -> { // configure advisors
                    advisorSpec.params(advisorParams);// pass parameters to advisors
                    advisorSpec.advisors(fileTreeContextAdvisor); // add file tree context advisor
                }).stream() // enable streaming
                .chatResponse() // get streaming chat responses
                .doOnNext(response -> { // for each streamed response chunk
                    String content = response.getResult().getOutput().getText(); // extract content from response
                    if(content != null && !content.isEmpty() && endTime.get() == 0L){ // set end time on first content receipt
                        endTime.set(System.currentTimeMillis());
                    }
                    if(response.getMetadata().getUsage()!=null){
                        usageRef.set(response.getMetadata().getUsage());
                    }
                    fullResponseBuffer.append(content); // accumulate the full response
                }).doOnComplete(() -> { // when streaming is complete
                    Schedulers.boundedElastic().schedule(() -> { // offload to separate thread
                        Long duration = (endTime.get() - startTime.get()) / 1000;
                        finalizeChats(userMessage, chatSession, fullResponseBuffer.toString(), duration, usageRef.get()); // finalize chat logs and handle file edits
                    });
                }).doOnError(error -> // handle errors
                    log.error("Error during AI chat response streaming for project: {}", projectId) // log error with project context
                ).map(response -> {
                    String text = response.getResult().getOutput().getText();
                    return new StreamResponse(text != null ? text : ""); // map to response text
                }); // map to response text
    }

    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long durationInSeconds, Usage usage) {
        // Implementation to finalize user chat logs, save messages, etc.
        Long projectId = chatSession.getProject().getId();
        if(usage != null) {
            int totalTokens = usage.getTotalTokens();
            usageService.recordTokenUsage(chatSession.getUser().getId(), totalTokens);
        }
        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .tokensUsed(usage.getPromptTokens())
                        .build()
        );

        ChatMessage assistantChatMessage = ChatMessage.builder()
                .role(MessageRole.ASSISTANT)
                .content("Assistant Message here...") // Placeholder, will be updated after parsing
                .chatSession(chatSession)
                .tokensUsed(usage.getCompletionTokens())
                .build();
        assistantChatMessage = chatMessageRepository.save(assistantChatMessage);

        List<ChatEvent> chatEventList = llmResponseParser.parseChatEvents(fullText, assistantChatMessage);
        chatEventList.addFirst(ChatEvent.builder()
                        .type(ChatEventType.THOUGHT)
                        .chatMessage(assistantChatMessage)
                        .content("Thought for "+durationInSeconds+"s")
                        .sequenceOrder(0)
                        .build());

        chatEventList.stream()
                .filter(e -> e.getType() == ChatEventType.FILE_EDIT)
                .forEach(e -> projectFileService.saveFile(projectId, e.getFilePath(), e.getContent()));

        chatEventRepository.saveAll(chatEventList);
    }

    private ChatSession createChatSessionIfNotExists(Long projectId, Long userId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);
        System.out.println("createChatSessionIfNotExists called");
        if(chatSession == null) {
            Project project = projectRepository.findById(projectId).orElseThrow(
                    () -> new ResourceNotFoundException("Project not found: " , projectId.toString())
            );
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new ResourceNotFoundException("User not found: ", userId.toString())
            );
            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .project(project)
                    .user(user)
                    .build();
            chatSession = chatSessionRepository.save(chatSession);
        }
        return chatSession;
    }

}
