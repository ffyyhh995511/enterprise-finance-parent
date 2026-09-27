package com.enterprise.finance.user.service;

import com.enterprise.finance.common.mq.MqTopics;
import com.enterprise.finance.common.mq.UserDemoMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * user 侧 RocketMQ 生产者。
 * RocketMQTemplate 由 rocketmq-spring-boot-starter 根据 application.yml 的
 * rocketmq.name-server + rocketmq.producer.group 自动装配，构造器注入即可。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserMqService {

    public static final int BATCH_SIZE = 100;

    /** starter 自动注册的模板，底层是 DefaultMQProducer */
    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 同步发送一批演示消息。syncSend 会等 Broker ACK，适合要明确成败的链路。
     * 生产还可按场景改用 asyncSend / sendOneWay / syncSendOrderly / 延迟消息 / 事务消息。
     */
    public Map<String, Object> sendBatch() {
        String batchId = UUID.randomUUID().toString().replace("-", "");
        long now = System.currentTimeMillis();
        for (int seq = 1; seq <= BATCH_SIZE; seq++) {
            UserDemoMessage message = new UserDemoMessage(batchId, seq, BATCH_SIZE, now);
            // 目的地只写 Topic；带 Tag 时写成 "user-demo-topic:TAG"
            rocketMQTemplate.syncSend(MqTopics.USER_DEMO, message);
        }
        log.info("sent {} rocketmq messages, topic={}, batchId={}", BATCH_SIZE, MqTopics.USER_DEMO, batchId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("topic", MqTopics.USER_DEMO);
        result.put("batchId", batchId);
        result.put("count", BATCH_SIZE);
        return result;
    }
}
