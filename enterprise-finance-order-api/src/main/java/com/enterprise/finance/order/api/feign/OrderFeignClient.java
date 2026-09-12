package com.enterprise.finance.order.api.feign;

import com.enterprise.finance.order.api.OrderApi;
import com.enterprise.finance.order.api.feign.fallback.OrderFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "enterprise-finance-order",
        contextId = "orderFeignClient",
        fallbackFactory = OrderFeignFallbackFactory.class
)
public interface OrderFeignClient extends OrderApi {
}
