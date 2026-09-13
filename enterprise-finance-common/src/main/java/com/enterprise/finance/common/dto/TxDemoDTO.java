package com.enterprise.finance.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TxDemoDTO {

    private Long id;
    private String tag;
    private String service;
}
