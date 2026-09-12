package com.enterprise.finance.product.config;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SentinelRuleConfig {

    @PostConstruct
    public void initRules() {
        List<DegradeRule> degradeRules = new ArrayList<>();
        DegradeRule errorRatio = new DegradeRule("productCircuit");
        errorRatio.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
        errorRatio.setCount(0.5);
        errorRatio.setTimeWindow(10);
        errorRatio.setMinRequestAmount(5);
        errorRatio.setStatIntervalMs(10_000);
        degradeRules.add(errorRatio);
        DegradeRuleManager.loadRules(degradeRules);

        List<FlowRule> flowRules = new ArrayList<>();
        FlowRule flowRule = new FlowRule("productFallback");
        flowRule.setGrade(1);
        flowRule.setCount(2);
        flowRules.add(flowRule);
        FlowRuleManager.loadRules(flowRules);
    }
}
