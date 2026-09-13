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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMqService {

    public static final int BATCH_SIZE = 100;

    private final RocketMQTemplate rocketMQTemplate;

    public Map<String, Object> sendBatch() {
        String batchId = UUID.randomUUID().toString().replace("-", "");
        long now = System.currentTimeMillis();
        for (int seq = 1; seq <= BATCH_SIZE; seq++) {
            UserDemoMessage message = new UserDemoMessage(batchId, seq, BATCH_SIZE, now);
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
