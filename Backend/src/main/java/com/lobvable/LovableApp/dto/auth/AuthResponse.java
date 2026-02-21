package com.lobvable.LovableApp.dto.auth;

public record AuthResponse(String token, UserProfileResponse user) { //record in java where all fields are final by default

}
