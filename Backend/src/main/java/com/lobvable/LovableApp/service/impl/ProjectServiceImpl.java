package com.lobvable.LovableApp.service.impl;

import com.lobvable.LovableApp.dto.project.ProjectRequest;
import com.lobvable.LovableApp.dto.project.ProjectResponse;
import com.lobvable.LovableApp.dto.project.ProjectSummaryResponse;
import com.lobvable.LovableApp.entity.Project;
import com.lobvable.LovableApp.entity.ProjectMember;
import com.lobvable.LovableApp.entity.ProjectMemberId;
import com.lobvable.LovableApp.entity.User;
import com.lobvable.LovableApp.enums.ProjectRole;
import com.lobvable.LovableApp.error.BadRequestException;
import com.lobvable.LovableApp.error.ResourceNotFoundException;
import com.lobvable.LovableApp.mapper.ProjectMapper;
import com.lobvable.LovableApp.repository.ProjectMemberRepository;
import com.lobvable.LovableApp.repository.ProjectRepository;
import com.lobvable.LovableApp.repository.UserRepository;
import com.lobvable.LovableApp.security.AuthUtil;
import com.lobvable.LovableApp.service.ProjectService;
import com.lobvable.LovableApp.service.ProjectTemplateService;
import com.lobvable.LovableApp.service.SubscriptionService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional // Ensures that all public methods are transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;
    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;
    SubscriptionService subscriptionService;
    ProjectTemplateService projectTemplateService;

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        Long userId = authUtil.getCurrentUserId();
        var projectsWithRole = projectRepository.findAllAccessibleByUser(userId);
        return projectsWithRole.stream()
                .map(p->projectMapper.toProjectSummaryResponse(p.getProject(), p.getRole()))
                .toList();
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)") //Spring expression language
    public ProjectSummaryResponse getUserProjectById(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);
        var projectWithRole = projectRepository.findAccessibleProjectByIdWithRole(projectId, userId)
                .orElseThrow(() -> new BadRequestException("Project Not Found"));
        return projectMapper.toProjectSummaryResponse(projectWithRole.getProject(), projectWithRole.getRole());
    }

    @Override
    public ProjectResponse createProject(ProjectRequest request) {
        if(!subscriptionService.canCreateNewProject()){
            throw new BadRequestException("Project creation limit reached for your current plan.");
        }

        Long userId = authUtil.getCurrentUserId();
        User owner = userRepository.getReferenceById(userId); //Using getReferenceById to avoid unnecessary DB hit

        Project project = Project.builder()
                .name(request.name())
                .isPublic(false)
                .build();

        project = projectRepository.save(project);
        ProjectMemberId projectMemberId= new ProjectMemberId(project.getId(), owner.getId());
        ProjectMember projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .projectRole(ProjectRole.OWNER)
                .user(owner)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .project(project)
                .build();
        projectMemberRepository.save(projectMember);
        projectTemplateService.initializeProjectFromTemplate(project.getId());
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        project.setName(request.name());
        project = projectRepository.save(project); //Not required updating is managed by JPA within a transaction
        return projectMapper.toProjectResponse(project);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public void softDelete(Long projectId) {
        Long userId = authUtil.getCurrentUserId();
        Project project = getAccessibleProjectById(projectId, userId);

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
    }

    // Internal functions
    public Project getAccessibleProjectById(Long projectId, Long userId) {
        return projectRepository.findAccessibleProjectById(projectId, userId).orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }
}
