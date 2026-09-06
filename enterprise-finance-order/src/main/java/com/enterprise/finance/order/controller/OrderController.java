package com.enterprise.finance.order.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.enterprise.finance.common.config.DemoProperties;
import com.enterprise.finance.common.dto.CreateOrderRequest;
import com.enterprise.finance.common.dto.DemoConfigDTO;
import com.enterprise.finance.common.dto.OrderDTO;
import com.enterprise.finance.common.dto.UserDTO;
import com.enterprise.finance.common.result.R;
import com.enterprise.finance.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
// @RequiredArgsConstructor：Lombok 注解，自动为 final 成员变量生成带参构造函数。
// 结合 Spring 4.3+ 特性，单构造函数会自动进行依赖注入（替代 @Autowired）。
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final DemoProperties demoProperties;

    @GetMapping("/{id}")
    public R<OrderDTO> getById(@PathVariable Long id) {
        return R.ok(orderService.getById(id));
    }

    @GetMapping("/list/{userId}")
    public R<List<OrderDTO>> listByUserId(@PathVariable Long userId) {
        return R.ok(orderService.listByUserId(userId));
    }

    @GetMapping("/user/{userId}")
    public R<UserDTO> user(@PathVariable Long userId) {
        return R.ok(orderService.getUser(userId));
    }

    @PostMapping("/create")
    public R<OrderDTO> create(@Valid @RequestBody CreateOrderRequest request) {
        return R.ok(orderService.create(request));
    }

    @GetMapping("/config")
    public R<DemoConfigDTO> config() {
        return R.ok(new DemoConfigDTO("enterprise-finance-order", demoProperties.getMessage(), demoProperties.getTimeoutMs()));
    }

    @GetMapping("/sentinel/circuit")
    @SentinelResource(value = "orderCircuit", blockHandler = "circuitBlock", fallback = "circuitFallback")
    public R<String> circuit(@RequestParam(defaultValue = "false") boolean fail,
                             @RequestParam(defaultValue = "0") long sleepMs) throws InterruptedException {
        if (sleepMs > 0) {
            Thread.sleep(sleepMs);
        }
        if (fail) {
            throw new IllegalStateException("simulated order circuit failure");
        }
        return R.ok("order circuit ok");
    }

    @GetMapping("/sentinel/fallback")
    @SentinelResource(value = "orderFallback", blockHandler = "fallbackBlock", fallback = "doFallback")
    public R<String> fallback(@RequestParam(defaultValue = "false") boolean fail) {
        if (fail) {
            throw new IllegalStateException("simulated order business failure");
        }
        return R.ok("order fallback resource ok");
    }

    public R<String> circuitBlock(boolean fail, long sleepMs, BlockException ex) {
        return R.fail("order circuit blocked: " + ex.getClass().getSimpleName());
    }

    public R<String> circuitFallback(boolean fail, long sleepMs, Throwable ex) {
        return R.fail("order circuit fallback: " + ex.getMessage());
    }

    public R<String> fallbackBlock(boolean fail, BlockException ex) {
        return R.fail("order fallback blocked: " + ex.getClass().getSimpleName());
    }

    public R<String> doFallback(boolean fail, Throwable ex) {
        return R.fail("order fallback: " + ex.getMessage());
    }
}
