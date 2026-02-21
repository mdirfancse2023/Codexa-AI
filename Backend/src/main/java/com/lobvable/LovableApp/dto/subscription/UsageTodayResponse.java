package com.lobvable.LovableApp.dto.subscription;

public record UsageTodayResponse(Integer tokensUsed, Integer tokenLimit, Integer previewsRunning, Integer previewLimit) {
}
