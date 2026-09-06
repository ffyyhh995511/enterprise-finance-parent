package com.enterprise.finance.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IdentifierDTO {
    private Long id;
    private String deviceId;
    private String idfa;
    private String anonId;
    private String appVersion;
    private LocalDateTime createTime;
    private Integer createTimeZone;
    private LocalDateTime updateTime;
    private Integer updateTimeZone;
}