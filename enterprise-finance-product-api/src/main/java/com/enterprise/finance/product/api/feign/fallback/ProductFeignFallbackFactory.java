package com.enterprise.finance.product.api.feign.fallback;

import com.enterprise.finance.common.exception.BusinessException;
import com.enterprise.finance.product.api.dto.ProductDTO;
import com.enterprise.finance.product.api.feign.ProductFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductFeignFallbackFactory implements FallbackFactory<ProductFeignClient> {

    @Override
    public ProductFeignClient create(Throwable cause) {
        return new ProductFeignClient() {
            @Override
            public ProductDTO getById(Long id) {
                log.warn("product-service getById fallback, id={}", id, cause);
                throw new BusinessException("product-service degraded: " + cause.getMessage());
            }

            @Override
            public ProductDTO deductStock(Long id, Integer quantity) {
                log.warn("product-service deductStock fallback, id={}, quantity={}", id, quantity, cause);
                throw new BusinessException("product-service deduct-stock degraded: " + cause.getMessage());
            }
        };
    }
}
