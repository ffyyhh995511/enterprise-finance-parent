package com.enterprise.finance.user.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IdentifierSaveRequest {

    @NotBlank(message = "device_id cannot be blank")
    private String deviceId;

    private String idfa;
    private String anonId;
    private String appVersion;
}
