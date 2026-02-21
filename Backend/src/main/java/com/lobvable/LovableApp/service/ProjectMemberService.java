package com.lobvable.LovableApp.service;

import com.lobvable.LovableApp.dto.member.InviteMemberRequest;
import com.lobvable.LovableApp.dto.member.MemberResponse;
import com.lobvable.LovableApp.dto.member.UpdateMemberRoleRequest;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest request);

    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest request);

    void removeProjectMember(Long projectId, Long memberId);
}
