package com.enterprise.finance.common.mq;

/**
 * 跨服务共享的 Topic 常量。生产建议 {域}-{实体}-{事件}，例如 order-paid-topic。
 */
public final class MqTopics {

    /** user 发送、order 消费的演示主题 */
    public static final String USER_DEMO = "user-demo-topic";

    private MqTopics() {
    }
}
