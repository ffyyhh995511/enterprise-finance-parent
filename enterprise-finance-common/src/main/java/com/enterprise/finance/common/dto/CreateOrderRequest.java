package com.enterprise.finance.common.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequest {

    @NotNull
    private Long userId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    private Boolean rollback = Boolean.FALSE;
}
