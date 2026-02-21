package com.lobvable.LovableApp.service.impl;

import com.lobvable.LovableApp.dto.subscription.PlanResponse;
import com.lobvable.LovableApp.service.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {
    @Override
    public List<PlanResponse> getAllActivePlans() {
        return List.of();
    }
}
