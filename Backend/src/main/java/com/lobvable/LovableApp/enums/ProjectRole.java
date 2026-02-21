package com.lobvable.LovableApp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import static com.lobvable.LovableApp.enums.ProjectPermission.*;

@RequiredArgsConstructor
@Getter
public enum ProjectRole {
    OWNER(VIEW, EDIT, DELETE, MANAGE_MEMBERS,VIEW_MEMBERS),
    VIEWER(VIEW,VIEW_MEMBERS),
    EDITOR(VIEW, EDIT, DELETE,VIEW_MEMBERS);

    ProjectRole(ProjectPermission... permissions) {
        this.permissions = Set.of(permissions);
    }

    private final Set<ProjectPermission> permissions;
}
