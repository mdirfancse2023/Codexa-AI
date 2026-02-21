package com.lobvable.LovableApp.service;
import com.lobvable.LovableApp.dto.subscription.CheckoutResponse;
import com.lobvable.LovableApp.dto.subscription.PortalResponse;
import com.lobvable.LovableApp.dto.subscription.SubscriptionResponse;
import com.lobvable.LovableApp.enums.SubscriptionStatus;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription();

    void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId);

    void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String gatewaySubscriptionId);

    void renewSubscriptionPeriod(String subId, Instant periodStart, Instant periodEnd);

    void markSubscriptionPastDue(String subId);

    boolean canCreateNewProject();
}
