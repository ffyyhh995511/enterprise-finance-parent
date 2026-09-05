package com.enterprise.finance.user.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.enterprise.finance.common.config.DemoProperties;
import com.enterprise.finance.common.dto.DemoConfigDTO;
import com.enterprise.finance.common.dto.OrderDTO;
import com.enterprise.finance.common.dto.UserDTO;
import com.enterprise.finance.common.result.R;
import com.enterprise.finance.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final DemoProperties demoProperties;

    @GetMapping("/{id}")
    public R<UserDTO> getById(@PathVariable Long id) {
        return R.ok(userService.getById(id));
    }

    @PostMapping("/{id}/deduct")
    public R<UserDTO> deduct(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return R.ok(userService.deduct(id, amount));
    }

    @GetMapping("/orders/{userId}")
    public R<List<OrderDTO>> orders(@PathVariable Long userId) {
        return R.ok(userService.listOrders(userId));
    }

    @GetMapping("/config")
    public R<DemoConfigDTO> config() {
        return R.ok(new DemoConfigDTO("enterprise-finance-user", demoProperties.getMessage(), demoProperties.getTimeoutMs()));
    }

    @GetMapping("/sentinel/circuit")
    @SentinelResource(value = "userCircuit", blockHandler = "circuitBlock", fallback = "circuitFallback")
    public R<String> circuit(@RequestParam(defaultValue = "false") boolean fail,
                             @RequestParam(defaultValue = "0") long sleepMs) throws InterruptedException {
        if (sleepMs > 0) {
            Thread.sleep(sleepMs);
        }
        if (fail) {
            throw new IllegalStateException("simulated user circuit failure");
        }
        return R.ok("user circuit ok");
    }

    @GetMapping("/sentinel/fallback")
    @SentinelResource(value = "userFallback", blockHandler = "fallbackBlock", fallback = "doFallback")
    public R<String> fallback(@RequestParam(defaultValue = "false") boolean fail) {
        if (fail) {
            throw new IllegalStateException("simulated user business failure");
        }
        return R.ok("user fallback resource ok");
    }

    public R<String> circuitBlock(boolean fail, long sleepMs, BlockException ex) {
        return R.fail("user circuit blocked: " + ex.getClass().getSimpleName());
    }

    public R<String> circuitFallback(boolean fail, long sleepMs, Throwable ex) {
        return R.fail("user circuit fallback: " + ex.getMessage());
    }

    public R<String> fallbackBlock(boolean fail, BlockException ex) {
        return R.fail("user fallback blocked: " + ex.getClass().getSimpleName());
    }

    public R<String> doFallback(boolean fail, Throwable ex) {
        return R.fail("user fallback: " + ex.getMessage());
    }
}
