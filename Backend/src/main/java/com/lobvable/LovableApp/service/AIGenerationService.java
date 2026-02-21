package com.lobvable.LovableApp.service;

import com.lobvable.LovableApp.dto.chat.StreamResponse;
import reactor.core.publisher.Flux;

public interface AIGenerationService {
   Flux<StreamResponse> streamResponse(String message, Long aLong);
}
