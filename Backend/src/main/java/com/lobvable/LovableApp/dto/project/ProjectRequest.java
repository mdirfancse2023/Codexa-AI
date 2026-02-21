package com.lobvable.LovableApp.dto.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectRequest(@NotBlank String name) {
}
