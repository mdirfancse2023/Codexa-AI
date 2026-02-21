package com.lobvable.LovableApp.service.impl;

import com.lobvable.LovableApp.dto.subscription.CheckoutResponse;
import com.lobvable.LovableApp.dto.subscription.PortalResponse;
import com.lobvable.LovableApp.dto.subscription.SubscriptionResponse;
import com.lobvable.LovableApp.entity.Plan;
import com.lobvable.LovableApp.entity.Subscription;
import com.lobvable.LovableApp.entity.User;
import com.lobvable.LovableApp.enums.SubscriptionStatus;
import com.lobvable.LovableApp.error.ResourceNotFoundException;
import com.lobvable.LovableApp.mapper.SubscriptionMapper;
import com.lobvable.LovableApp.repository.*;
import com.lobvable.LovableApp.security.AuthUtil;
import com.lobvable.LovableApp.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    private final ProjectRepository projectRepository;

    private final AuthUtil authUtil;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final UserRepository  userRepository;
    private final PlanRepository planRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final Integer FREE_TIER_PROJECT_ALLOWED = 10;

    @Override
    public SubscriptionResponse getCurrentSubscription() {
        Long userId = authUtil.getCurrentUserId();
        var currentSubscription =  subscriptionRepository.findByUserIdAndStatusIn(userId, Set.of(SubscriptionStatus.ACTIVE,
                SubscriptionStatus.PAST_DUE,
                SubscriptionStatus.TRIALING)).orElse(new Subscription());

        return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {
        boolean exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if (exists) {
            return;
        }
        User user = getUser(userId);
        Plan plan = getPlan(planId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .stripeSubscriptionId(subscriptionId)
                .status(SubscriptionStatus.INCOMPLETE)
                .build();

        subscriptionRepository.save(subscription);

    }

    @Override
    public void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);
        boolean hasSubscriptionBeenUpdated = false;
        if(status!=null && status!=subscription.getStatus()){
            subscription.setStatus(status);
            hasSubscriptionBeenUpdated = true;
        }
        if(periodStart!=null && !periodStart.equals(subscription.getCurrentPeriodStart()) ){
            subscription.setCurrentPeriodStart(periodStart);
            hasSubscriptionBeenUpdated = true;
        }
        if(periodEnd!=null && !periodEnd.equals(subscription.getCurrentPeriodEnd())){
            subscription.setCurrentPeriodEnd(periodEnd);
            hasSubscriptionBeenUpdated = true;
        }
        if(cancelAtPeriodEnd!=null && cancelAtPeriodEnd!=subscription.getCancelAtPeriodEnd()){
            subscription.setCancelAtPeriodEnd(cancelAtPeriodEnd);
            hasSubscriptionBeenUpdated = true;
        }
        if(planId!=null && !planId.equals(subscription.getPlan().getId())) {
            Plan newPlan = getPlan(planId);
            subscription.setPlan(newPlan);
            hasSubscriptionBeenUpdated = true;
        }
        if(hasSubscriptionBeenUpdated){
            log.debug("Updating subscription {} with new details", gatewaySubscriptionId);
        }
    }

    @Override
    public void cancelSubscription(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscriptionRepository.save(subscription);
    }

    @Override
    public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);
        Instant newStart = periodStart != null ? periodStart : subscription.getCurrentPeriodStart();
        subscription.setCurrentPeriodStart(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if(subscription.getStatus() == SubscriptionStatus.PAST_DUE || subscription.getStatus() == SubscriptionStatus.INCOMPLETE){
            subscription.setStatus(SubscriptionStatus.ACTIVE);
        }

        subscriptionRepository.save(subscription);
    }

    @Override
    public void markSubscriptionPastDue(String gatewaySubscriptionId) {
        Subscription subscription = getSubscription(gatewaySubscriptionId);
        if(subscription.getStatus()==SubscriptionStatus.PAST_DUE){
            log.debug("Subscription {} is already marked as PAST_DUE", gatewaySubscriptionId);
            return;
        }
        subscription.setStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);

        //Notify via email
    }

    @Override
    public boolean canCreateNewProject() {
        Long userId = authUtil.getCurrentUserId();
        SubscriptionResponse currentSubscription = getCurrentSubscription();
        int countOfOwnedProjects = projectMemberRepository.countProjectOwnedByUser(userId);
        if(currentSubscription.plan()==null){
            return countOfOwnedProjects < FREE_TIER_PROJECT_ALLOWED;
        }
        return countOfOwnedProjects < currentSubscription.plan().maxProjects();
    }

    // Helper methods
    private User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User", userId.toString()));
    }

    private Plan getPlan(Long planId){
        return planRepository.findById(planId)
                .orElseThrow(()-> new ResourceNotFoundException("Plan", planId.toString()));
    }

    private Subscription getSubscription(String gatewaySubscriptionId) {
        return subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", gatewaySubscriptionId));
    }


}
