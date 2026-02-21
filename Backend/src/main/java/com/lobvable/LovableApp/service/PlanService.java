package com.lobvable.LovableApp.service;
import com.lobvable.LovableApp.dto.subscription.PlanResponse;

import java.util.List;

public interface PlanService {
    List<PlanResponse> getAllActivePlans();
}
