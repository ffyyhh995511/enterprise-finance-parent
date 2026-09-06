package com.enterprise.finance.common.dto;

import lombok.Data;

@Data
public class PageRequest {
    private long current = 1;
    private long size = 10;
}