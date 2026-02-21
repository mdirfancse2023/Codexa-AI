package com.lobvable.LovableApp.mapper;

import com.lobvable.LovableApp.dto.auth.SignupRequest;
import com.lobvable.LovableApp.dto.auth.UserProfileResponse;
import com.lobvable.LovableApp.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(SignupRequest signupRequest);
    UserProfileResponse toUserProfileResponse(User user);
}
