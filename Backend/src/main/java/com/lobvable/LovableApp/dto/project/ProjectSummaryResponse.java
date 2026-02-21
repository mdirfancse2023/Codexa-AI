package com.lobvable.LovableApp.dto.project;

import com.lobvable.LovableApp.enums.ProjectRole;

import java.time.Instant;

public record ProjectSummaryResponse(Long id, String name, Instant createdAt, Instant updatedAt, ProjectRole role) {
}
