package com.enterprise.finance.order.mq;

import com.enterprise.finance.common.mq.MqTopics;
import com.enterprise.finance.common.mq.UserDemoMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(topic = MqTopics.USER_DEMO, consumerGroup = "order-user-demo-consumer")
public class UserDemoMessageConsumer implements RocketMQListener<UserDemoMessage> {

    @Override
    public void onMessage(UserDemoMessage message) {
        log.info("consume user demo mq: batchId={}, seq={}/{}",
                message.getBatchId(), message.getSeq(), message.getTotal());
    }
}
