package com.lobvable.LovableApp.controller;

import com.lobvable.LovableApp.dto.member.InviteMemberRequest;
import com.lobvable.LovableApp.dto.member.MemberResponse;
import com.lobvable.LovableApp.dto.member.UpdateMemberRoleRequest;
import com.lobvable.LovableApp.entity.ProjectMember;
import com.lobvable.LovableApp.service.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@RequiredArgsConstructor
public class ProjectMemberController {

    //Controller methods to be implemented
    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMembers(@PathVariable Long projectId){
        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember(@PathVariable Long projectId, @RequestBody @Valid InviteMemberRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMemberService.inviteMember(projectId, request)); // Placeholder response
    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(@PathVariable Long projectId, @PathVariable Long memberId, @RequestBody @Valid UpdateMemberRoleRequest request) {
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId, memberId, request)); // Placeholder response
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long projectId, @PathVariable Long memberId) {
        projectMemberService.removeProjectMember(projectId, memberId);
        return ResponseEntity.noContent().build(); // Placeholder response
    }
}
