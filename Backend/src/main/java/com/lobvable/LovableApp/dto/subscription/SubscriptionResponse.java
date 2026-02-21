package com.lobvable.LovableApp.dto.subscription;

import java.time.Instant;

public record SubscriptionResponse(PlanResponse plan, String status, Instant currentPeriodEnd, Long tokensUsedThisCycle) {
}
