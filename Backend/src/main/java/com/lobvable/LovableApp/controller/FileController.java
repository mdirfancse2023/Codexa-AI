package com.lobvable.LovableApp.controller;

import com.lobvable.LovableApp.dto.project.FileContentResponse;
import com.lobvable.LovableApp.dto.project.FileNode;
import com.lobvable.LovableApp.dto.project.FileTreeResponse;
import com.lobvable.LovableApp.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/files")
@RequiredArgsConstructor
public class FileController {

    private final ProjectFileService projectFileService;

    @GetMapping
    public ResponseEntity<FileTreeResponse> getFileTree(@PathVariable Long projectId){
        return ResponseEntity.ok(projectFileService.getFileTree(projectId));
    }

    @GetMapping("/content") // Catch-all for any file path
    public ResponseEntity<FileContentResponse> getFile(@PathVariable Long projectId, @RequestParam String path){
        return ResponseEntity.ok(projectFileService.getFileContent(projectId, path));

    }

}
