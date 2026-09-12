package com.enterprise.finance.user.api.feign;

import com.enterprise.finance.user.api.UserApi;
import com.enterprise.finance.user.api.feign.fallback.UserFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "enterprise-finance-user",
        contextId = "userFeignClient",
        fallbackFactory = UserFeignFallbackFactory.class
)
public interface UserFeignClient extends UserApi {
}
