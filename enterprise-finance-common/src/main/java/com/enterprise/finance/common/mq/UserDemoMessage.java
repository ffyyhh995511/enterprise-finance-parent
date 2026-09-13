package com.enterprise.finance.common.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDemoMessage {

    private String batchId;
    private int seq;
    private int total;
    private long timestamp;
}
