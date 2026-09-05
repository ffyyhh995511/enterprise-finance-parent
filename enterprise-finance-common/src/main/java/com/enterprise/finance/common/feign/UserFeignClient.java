package com.enterprise.finance.common.feign;

import com.enterprise.finance.common.dto.UserDTO;
import com.enterprise.finance.common.feign.fallback.UserFeignFallbackFactory;
import com.enterprise.finance.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(
        name = "enterprise-finance-user",
        contextId = "userFeignClient",
        fallbackFactory = UserFeignFallbackFactory.class
)
public interface UserFeignClient {

    @GetMapping("/user/{id}")
    R<UserDTO> getById(@PathVariable("id") Long id);

    @PostMapping("/user/{id}/deduct")
    R<UserDTO> deduct(@PathVariable("id") Long id, @RequestParam("amount") BigDecimal amount);
}
