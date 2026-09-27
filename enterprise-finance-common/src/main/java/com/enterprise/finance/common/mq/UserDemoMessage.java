package com.enterprise.finance.common.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 演示消息体。真实业务还应带业务主键（如 orderNo），供消费端幂等。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDemoMessage {

    /** 同一批次的唯一标识，可用于对账或幂等 */
    private String batchId;
    /** 批次内序号，从 1 开始 */
    private int seq;
    /** 本批次总条数 */
    private int total;
    /** 发送端时间戳 */
    private long timestamp;
}
