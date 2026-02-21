package com.lobvable.LovableApp.service;

import com.lobvable.LovableApp.dto.project.ProjectRequest;
import com.lobvable.LovableApp.dto.project.ProjectResponse;
import com.lobvable.LovableApp.dto.project.ProjectSummaryResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectService {
    List<ProjectSummaryResponse> getUserProjects();

    ProjectSummaryResponse getUserProjectById(Long id);

    ProjectResponse createProject(ProjectRequest request);

    ProjectResponse updateProject(Long id, ProjectRequest request);

    void softDelete(Long id);
}
