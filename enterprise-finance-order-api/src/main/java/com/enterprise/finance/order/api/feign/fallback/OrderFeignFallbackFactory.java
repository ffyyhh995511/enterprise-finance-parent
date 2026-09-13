package com.enterprise.finance.order.api.feign.fallback;

import com.enterprise.finance.common.dto.TxDemoDTO;
import com.enterprise.finance.common.exception.BusinessException;
import com.enterprise.finance.order.api.dto.CreateOrderRequest;
import com.enterprise.finance.order.api.dto.OrderDTO;
import com.enterprise.finance.order.api.feign.OrderFeignClient;
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
            public OrderDTO getById(Long id) {
                log.warn("order-service getById fallback, id={}", id, cause);
                throw new BusinessException("order-service degraded: " + cause.getMessage());
            }

            @Override
            public List<OrderDTO> listByUserId(Long userId) {
                log.warn("order-service listByUserId fallback, userId={}", userId, cause);
                throw new BusinessException("order-service list degraded: " + cause.getMessage());
            }

            @Override
            public OrderDTO create(CreateOrderRequest request) {
                log.warn("order-service create fallback, userId={}", request != null ? request.getUserId() : null, cause);
                throw new BusinessException("order-service create degraded: " + cause.getMessage());
            }

            @Override
            public TxDemoDTO insertTxDemo(String tag) {
                log.warn("order-service insertTxDemo fallback, tag={}", tag, cause);
                throw new BusinessException("order-service tx-demo degraded: " + cause.getMessage());
            }

            @Override
            public List<TxDemoDTO> listTxDemo(String tag) {
                log.warn("order-service listTxDemo fallback, tag={}", tag, cause);
                throw new BusinessException("order-service tx-demo list degraded: " + cause.getMessage());
            }
        };
    }
}
