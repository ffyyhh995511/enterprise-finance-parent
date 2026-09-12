package com.enterprise.finance.user.api.dto;

import com.enterprise.finance.common.dto.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IdentifierPageRequest extends PageRequest {
    private String deviceId;
}
