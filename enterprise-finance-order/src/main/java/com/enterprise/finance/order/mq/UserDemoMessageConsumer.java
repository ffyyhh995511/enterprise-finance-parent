package com.enterprise.finance.order.mq;

import com.enterprise.finance.common.mq.MqTopics;
import com.enterprise.finance.common.mq.UserDemoMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * order 侧 Push 消费者。线程数、重试次数等 starter 不支持写在 yml，只能写在本注解上。
 * 同一 Topic 多个下游必须用不同 consumerGroup，否则会抢消息。
 * 重试耗尽后进入死信 Topic：%DLQ%order-user-demo-consumer。
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = MqTopics.USER_DEMO,
        consumerGroup = "order-user-demo-consumer",
        selectorType = SelectorType.TAG,
        selectorExpression = "*",
        consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel = MessageModel.CLUSTERING,
        consumeThreadNumber = 10,
        consumeThreadMax = 20,
        maxReconsumeTimes = 3,
        consumeTimeout = 15,
        awaitTerminationMillisWhenShutdown = 30000,
        enableMsgTrace = true
)
public class UserDemoMessageConsumer implements RocketMQListener<UserDemoMessage> {

    /**
     * 集群 + 重试可能导致重复投递，真实业务需按 batchId+seq（或业务主键）做幂等。
     */
    @Override
    public void onMessage(UserDemoMessage message) {
        log.info("consume user demo mq: batchId={}, seq={}/{}",
                message.getBatchId(), message.getSeq(), message.getTotal());
    }
}
