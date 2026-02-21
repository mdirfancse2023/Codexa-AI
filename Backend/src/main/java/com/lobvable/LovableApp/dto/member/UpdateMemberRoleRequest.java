package com.lobvable.LovableApp.dto.member;

import com.lobvable.LovableApp.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

//To be updated
public record UpdateMemberRoleRequest(@NotNull ProjectRole role) {
}
