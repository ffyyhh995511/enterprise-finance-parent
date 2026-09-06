package com.enterprise.finance.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IdentifierPageRequest extends PageRequest {
    private String deviceId;
}