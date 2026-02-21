package com.lobvable.LovableApp.service;

import com.lobvable.LovableApp.dto.deploy.DeployResponse;

public interface DeploymentService {
    DeployResponse deploy(Long projectId);
}
