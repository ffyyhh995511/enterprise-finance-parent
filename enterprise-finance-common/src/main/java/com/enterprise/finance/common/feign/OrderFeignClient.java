package com.enterprise.finance.common.feign;

import com.enterprise.finance.common.dto.OrderDTO;
import com.enterprise.finance.common.feign.fallback.OrderFeignFallbackFactory;
import com.enterprise.finance.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "enterprise-finance-order",
        contextId = "orderFeignClient",
        fallbackFactory = OrderFeignFallbackFactory.class
)
public interface OrderFeignClient {

    @GetMapping("/order/{id}")
    R<OrderDTO> getById(@PathVariable("id") Long id);

    @GetMapping("/order/list/{userId}")
    R<List<OrderDTO>> listByUserId(@PathVariable("userId") Long userId);
}
