package com.lobvable.LovableApp.mapper;

import com.lobvable.LovableApp.dto.project.FileNode;
import com.lobvable.LovableApp.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {
    List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList);
}
