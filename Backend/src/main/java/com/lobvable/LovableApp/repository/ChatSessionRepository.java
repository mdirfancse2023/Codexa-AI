package com.lobvable.LovableApp.repository;

import com.lobvable.LovableApp.entity.ChatSession;
import com.lobvable.LovableApp.entity.ChatSessionId;
import com.lobvable.LovableApp.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, ChatSessionId> {
}
