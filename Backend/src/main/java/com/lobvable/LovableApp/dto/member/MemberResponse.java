package com.lobvable.LovableApp.dto.member;

import com.lobvable.LovableApp.enums.ProjectRole;

import java.time.Instant;

public record MemberResponse(Long userId, String username, String name,  ProjectRole role, Instant invitedAt) {
}
