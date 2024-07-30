package com.example.pampam.orders.model.entity;

public class PaymentValidationResult {
    private final boolean isValid;
    private final PaymentProducts paymentProducts;
    private final Integer amount;

    public PaymentValidationResult(boolean isValid, PaymentProducts paymentProducts, Integer amount) {
        this.isValid = isValid;
        this.paymentProducts = paymentProducts;
        this.amount = amount;
    }

    public boolean isValid() {
        return isValid;
    }

    public PaymentProducts getPaymentProducts() {
        return paymentProducts;
    }

    public Integer getAmount() {
        return amount;
    }
}
