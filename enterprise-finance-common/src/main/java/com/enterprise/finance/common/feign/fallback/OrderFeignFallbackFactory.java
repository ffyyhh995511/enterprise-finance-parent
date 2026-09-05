package com.enterprise.finance.common.feign.fallback;

import com.enterprise.finance.common.dto.OrderDTO;
import com.enterprise.finance.common.feign.OrderFeignClient;
import com.enterprise.finance.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class OrderFeignFallbackFactory implements FallbackFactory<OrderFeignClient> {

    @Override
    public OrderFeignClient create(Throwable cause) {
        return new OrderFeignClient() {
            @Override
            public R<OrderDTO> getById(Long id) {
                log.warn("order-service getById fallback, id={}", id, cause);
                return R.fail("order-service degraded: " + cause.getMessage());
            }

            @Override
            public R<List<OrderDTO>> listByUserId(Long userId) {
                log.warn("order-service listByUserId fallback, userId={}", userId, cause);
                return R.fail("order-service list degraded: " + cause.getMessage());
            }
        };
    }
}
