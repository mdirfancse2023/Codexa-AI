package com.lobvable.LovableApp.service;

import com.lobvable.LovableApp.dto.project.FileContentResponse;
import com.lobvable.LovableApp.dto.project.FileNode;
import com.lobvable.LovableApp.dto.project.FileTreeResponse;

import java.util.List;

public interface ProjectFileService {
    FileTreeResponse getFileTree(Long projectId);

    FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
