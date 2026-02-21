package com.lobvable.LovableApp.service;

import com.lobvable.LovableApp.dto.subscription.PlanLimitsResponse;
import com.lobvable.LovableApp.dto.subscription.UsageTodayResponse;
import org.jspecify.annotations.Nullable;

public interface UsageService {
    void recordTokenUsage(Long userId, int actualTokens);
    void checkDailyTokensUsage();
}
