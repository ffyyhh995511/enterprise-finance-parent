package com.enterprise.finance.product.api.feign;

import com.enterprise.finance.product.api.ProductApi;
import com.enterprise.finance.product.api.feign.fallback.ProductFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "enterprise-finance-product",
        contextId = "productFeignClient",
        fallbackFactory = ProductFeignFallbackFactory.class
)
public interface ProductFeignClient extends ProductApi {
}
