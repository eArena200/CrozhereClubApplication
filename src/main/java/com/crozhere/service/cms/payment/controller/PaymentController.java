package com.crozhere.service.cms.payment.controller;

import com.crozhere.service.cms.payment.controller.model.request.InitPaymentRequest;
import com.crozhere.service.cms.payment.controller.model.response.PaymentResponse;
import com.crozhere.service.cms.payment.repository.entity.Payment;
import com.crozhere.service.cms.payment.service.PaymentService;
import com.crozhere.service.cms.booking.service.exception.PaymentServiceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payment")
@Tag(name = "Payment Management", description = "APIs for managing booking payments on behalf of a club")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(
            summary = "Initiate payment",
            description = "Initiates a payment for a booking intent. Currently only supports CASH payment."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment successfully recorded and booking confirmed"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @Parameter(
                    description = "Payment initiation request",
                    required = true)
            @RequestBody InitPaymentRequest request) {
        try {
            Payment payment = paymentService.initPayment(request);
            return ResponseEntity.ok(toPaymentResponse(payment));
        } catch (PaymentServiceException e) {
            log.error("Error initiating payment", e);
            throw new RuntimeException(e); // Replace with global handler if needed
        }
    }

    @Operation(
            summary = "Get payment by ID",
            description = "Fetches payment details by payment ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment details fetched successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Payment not found"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error")
    })
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @Parameter(
                    description = "ID of the payment to retrieve",
                    required = true)
            @PathVariable Long paymentId) {
        try {
            Payment payment = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(toPaymentResponse(payment));
        } catch (PaymentServiceException e) {
            log.error("Error fetching payment", e);
            throw new RuntimeException(e);
        }
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .intentId(payment.getIntentId())
                .paymentMode(payment.getPaymentMode())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .build();
    }
}

