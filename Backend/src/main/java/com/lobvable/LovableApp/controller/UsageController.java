package com.lobvable.LovableApp.controller;

import com.lobvable.LovableApp.dto.subscription.PlanLimitsResponse;
import com.lobvable.LovableApp.dto.subscription.UsageTodayResponse;
import com.lobvable.LovableApp.service.UsageService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
public class UsageController {
    private final UsageService usageService;

//    @GetMapping("/today")
//    public ResponseEntity<UsageTodayResponse> getTodayUsage() {
//        Long userId = 1L; // TODO: get user id from security context
//       return ResponseEntity.ok(usageService.getTodayUsageOfUser(userId));
//    }

}
