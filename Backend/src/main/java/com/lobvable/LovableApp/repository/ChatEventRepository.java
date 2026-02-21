package com.lobvable.LovableApp.repository;

import com.lobvable.LovableApp.entity.ChatEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatEventRepository extends JpaRepository<ChatEvent, Long> {
}
