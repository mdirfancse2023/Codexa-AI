package com.lobvable.LovableApp.mapper;

import com.lobvable.LovableApp.dto.member.MemberResponse;
import com.lobvable.LovableApp.entity.ProjectMember;
import com.lobvable.LovableApp.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMemberMapper {

    @Mapping(target="userId",source="id")
    @Mapping(target="role", constant="OWNER")
    MemberResponse toProjectMemberResponseFromOwner(User owner);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "projectRole", target = "role")
    MemberResponse toProjectMemberResponseFromMember(ProjectMember projectMember);
}
