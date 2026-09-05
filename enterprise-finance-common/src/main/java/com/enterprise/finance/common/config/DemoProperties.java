package com.enterprise.finance.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Data
@RefreshScope
@ConfigurationProperties(prefix = "demo")
public class DemoProperties {

    private String message = "local default";
    private Long timeoutMs = 0L;
}
