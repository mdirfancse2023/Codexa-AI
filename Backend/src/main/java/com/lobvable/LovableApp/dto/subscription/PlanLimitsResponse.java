package com.lobvable.LovableApp.dto.subscription;

public record PlanLimitsResponse(String planName, Integer maxTokensPerDay, Integer maxProjects, Boolean unlimitedAi) {
}
