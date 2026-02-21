package com.lobvable.LovableApp.mapper;

import com.lobvable.LovableApp.dto.project.ProjectResponse;
import com.lobvable.LovableApp.dto.project.ProjectSummaryResponse;
import com.lobvable.LovableApp.entity.Project;
import com.lobvable.LovableApp.enums.ProjectRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectResponse toProjectResponse(Project project);

    //@Mapping(source="name", target="projectName")
    //@Mapping(target="createdAt", dateFormat = "yyyy-MM-dd")
    ProjectSummaryResponse toProjectSummaryResponse(Project project, ProjectRole role);
    List<ProjectSummaryResponse> toListOfProjectSummaryResponses(List<Project> projects);
}
