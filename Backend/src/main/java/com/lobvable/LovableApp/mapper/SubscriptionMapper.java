package com.lobvable.LovableApp.mapper;

import com.lobvable.LovableApp.dto.subscription.PlanResponse;
import com.lobvable.LovableApp.dto.subscription.SubscriptionResponse;
import com.lobvable.LovableApp.entity.Plan;
import com.lobvable.LovableApp.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionResponse toSubscriptionResponse(Subscription subscription);

    PlanResponse toPlanResponse(Plan plan);
}
