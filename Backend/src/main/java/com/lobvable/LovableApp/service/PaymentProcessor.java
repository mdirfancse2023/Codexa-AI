package com.lobvable.LovableApp.service;

import com.lobvable.LovableApp.dto.subscription.CheckoutRequest;
import com.lobvable.LovableApp.dto.subscription.CheckoutResponse;
import com.lobvable.LovableApp.dto.subscription.PortalResponse;
import com.stripe.model.StripeObject;

import java.util.Map;

public interface PaymentProcessor {

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest request);

    PortalResponse openCustomerPortal();


    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metadata);
}
