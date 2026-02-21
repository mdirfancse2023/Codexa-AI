package com.lobvable.LovableApp.service;

import com.lobvable.LovableApp.dto.auth.UserProfileResponse;
import org.jspecify.annotations.Nullable;

public interface UserService {
    UserProfileResponse getProfile(Long userid);
}
