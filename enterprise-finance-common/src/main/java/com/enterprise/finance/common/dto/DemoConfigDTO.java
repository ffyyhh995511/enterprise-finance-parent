package com.enterprise.finance.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoConfigDTO {

    private String service;
    private String message;
    private Long timeoutMs;
}
